package com.example.sample.web.bean;

import com.example.sample.dto.DetailRowView;
import com.example.sample.dto.DetailSubmitForm;
import com.example.sample.exception.BusinessException;
import com.example.sample.service.DetailService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailListBeanSubmitTest {

    private static final String U1 = "u1";
    private static final String DEFAULT_USER = "user1";
    private static final List<Long> SELECTED = List.of(10L, 20L);

    private static final String MSG_INFO_DONE = "申請が完了しました。";
    private static final String MSG_WARN_BIZ = "何らかの業務エラー";
    private static final String MSG_ERROR_UNEXPECTED = "エラーが発生しました。管理者に連絡してください。";

    @Mock DetailService detailService;
    @Mock FacesContext facesContext;

    @Captor ArgumentCaptor<FacesMessage> msgCaptor;

    DetailListBean bean;
    DetailSubmitForm form;

    @BeforeEach
    void setUp() {
        bean = new DetailListBean(detailService, facesContext);

        form = new DetailSubmitForm();
        form.setLoginUserId(U1);
        // デフォルト選択
        form.getSelected().put(10L, true);
        form.getSelected().put(20L, true);
        form.getSelected().put(30L, false); // 画面外同期の検証用

        bean.setForm(form);
    }

    @Nested
    @DisplayName("submit()")
    class Submit {

        @Test
        @DisplayName("正常系: apply成功 → INFOメッセージ, clearSelections, reloadRows実行（順序も担保）")
        void success() {
            // given
            stubRowsForUser(U1, 10L, 20L);

            // when
            bean.submit();

            // then
            // apply 引数
            verify(detailService, times(1)).apply(SELECTED, U1);

            // メッセージ
            assertMessage(FacesMessage.SEVERITY_INFO, MSG_INFO_DONE, 1);

            // 選択が落ちる & 画面外が消える
            assertEquals(Boolean.FALSE, form.getSelected().get(10L));
            assertEquals(Boolean.FALSE, form.getSelected().get(20L));
            assertFalse(form.getSelected().containsKey(30L));

            // reloadRows（= getListForLoginUser）1回
            verifyReloadCalledFor(U1, 1);

            // 呼び出し順（apply → addMessage → getList）
            InOrder inOrder = inOrder(detailService, facesContext);
            inOrder.verify(detailService).apply(SELECTED, U1);
            inOrder.verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            inOrder.verify(detailService).getListForLoginUser(U1, null);

            verifyNoMoreInteractions(detailService, facesContext);
        }

        @Test
        @DisplayName("業務例外: BusinessException → WARNメッセージ, 選択維持, reloadRowsなし")
        void businessException() {
            // given
            doThrow(new BusinessException(MSG_WARN_BIZ))
                    .when(detailService).apply(anyList(), anyString());

            // when
            bean.submit();

            // then
            verify(detailService, times(1)).apply(SELECTED, U1);
            verify(detailService, never()).getListForLoginUser(anyString(), any());
            assertMessage(FacesMessage.SEVERITY_WARN, MSG_WARN_BIZ, 1);

            // 選択維持
            assertEquals(Boolean.TRUE, form.getSelected().get(10L));
            assertEquals(Boolean.TRUE, form.getSelected().get(20L));

            verifyNoMoreInteractions(detailService, facesContext);
        }

        @Test
        @DisplayName("予期せぬ例外: RuntimeException → ERRORメッセージ, 選択維持, reloadRowsなし")
        void unexpectedException() {
            // given
            doThrow(new RuntimeException("DB down"))
                    .when(detailService).apply(anyList(), anyString());

            // when
            bean.submit();

            // then
            verify(detailService, times(1)).apply(SELECTED, U1);
            verify(detailService, never()).getListForLoginUser(anyString(), any());
            assertMessage(FacesMessage.SEVERITY_ERROR, MSG_ERROR_UNEXPECTED, 1);

            // 選択維持
            assertEquals(Boolean.TRUE, form.getSelected().get(10L));
            assertEquals(Boolean.TRUE, form.getSelected().get(20L));

            verifyNoMoreInteractions(detailService, facesContext);
        }

        @Test
        @DisplayName("選択ゼロ: 空のselectedIdsでも apply → INFO → reloadRows")
        void emptySelection() {
            // given: すべて未選択
            form.getSelected().clear();
            form.getSelected().put(10L, false);
            form.getSelected().put(20L, false);
            stubRowsForUser(U1, 10L, 20L);

            // when
            bean.submit();

            // then: 空リストで apply される
            verify(detailService, times(1))
                    .apply(argThat(List::isEmpty), eq(U1));

            assertMessage(FacesMessage.SEVERITY_INFO, MSG_INFO_DONE, 1);
            verifyReloadCalledFor(U1, 1);

            verifyNoMoreInteractions(detailService, facesContext);
        }

        @Test
        @DisplayName("initの補正: loginUserIdがblank → DEFAULT_USER で apply")
        void afterInit_usesDefaultUserId() {
            // given: blank → init で補正（init内で1回 reloadRows 実行）
            form.setLoginUserId("   ");
            bean.init();

            // init フェーズの相互作用を切り離し、submit フェーズのみ検証
            reset(detailService, facesContext);

            stubRowsForUser(DEFAULT_USER, 10L, 20L);
            form.getSelected().clear();
            form.getSelected().put(10L, true);
            form.getSelected().put(20L, true);

            // when
            bean.submit();

            // then
            verify(detailService, times(1)).apply(List.of(10L, 20L), DEFAULT_USER);
            assertMessage(FacesMessage.SEVERITY_INFO, MSG_INFO_DONE, 1);
            verifyReloadCalledFor(DEFAULT_USER, 1);

            // 呼び出し順（apply → addMessage → getList）
            InOrder inOrder = inOrder(detailService, facesContext);
            inOrder.verify(detailService).apply(List.of(10L, 20L), DEFAULT_USER);
            inOrder.verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            inOrder.verify(detailService).getListForLoginUser(DEFAULT_USER, null);

            verifyNoMoreInteractions(detailService, facesContext);
        }
    }

    // ===== helpers =====

    private void stubRowsForUser(String userId, Long... ids) {
        var rows = Stream.of(ids).map(this::row).toList();
        given(detailService.getListForLoginUser(eq(userId), isNull()))
                .willReturn(rows);
    }

    private DetailRowView row(long id) {
        var r = mock(DetailRowView.class);
        when(r.getDetailId()).thenReturn(id);
        return r;
    }

    private void assertMessage(FacesMessage.Severity severity, String summary, int times) {
        verify(facesContext, times(times)).addMessage(eq(null), msgCaptor.capture());
        var last = msgCaptor.getValue();
        assertSame(severity, last.getSeverity());
        assertEquals(summary, last.getSummary());
    }

    private void verifyReloadCalledFor(String userId, int times) {
        verify(detailService, times(times)).getListForLoginUser(userId, null);
    }
}
