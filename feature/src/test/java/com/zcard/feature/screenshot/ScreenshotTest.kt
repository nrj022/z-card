package com.zcard.feature.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.domain.model.Asset
import com.zcard.domain.model.AssetType
import com.zcard.domain.model.CardPreview
import com.zcard.domain.model.TextElement
import com.zcard.feature.cardeditor.CardEditorContent
import com.zcard.feature.cardeditor.dialog.ObjectDeleteConfirmDialog
import com.zcard.feature.cardeditor.dialog.UnsavedChangesDialog
import com.zcard.feature.cardeditor.dialog.UploadCancelConfirmDialog
import com.zcard.feature.cardeditor.textedit.TextEditContent
import com.zcard.feature.cardeditor.transform.TransformContent
import com.zcard.feature.cardshare.CardShareContent
import com.zcard.feature.home.HomeContent
import com.zcard.feature.setting.SettingContent
import com.zcard.feature.tutorial.TutorialContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.concurrent.TimeUnit

/**
 * Roborazzi 스크린샷 테스트
 *
 * - 기준 이미지 생성: ./gradlew :feature:recordRoborazziDebug
 * - 변경 비교:       ./gradlew :feature:compareRoborazziDebug
 * - 결과 위치:       feature/build/outputs/roborazzi
 * - 일반 test 태스크에서는 제외되고, Roborazzi 태스크 실행 시에만 동작 (feature/build.gradle.kts 참고)
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    // 상대 시간 표기("3 hours ago")가 실행 시점과 무관하게 고정되도록 현재 기준으로 생성
    private val sampleCards: List<CardPreview>
        get() {
            val updatedAt = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3)
            return (1L..4L).map { id -> CardPreview(cardId = id, title = "Card $id", updatedAt = updatedAt) }
        }

    private val sampleAssets: List<Asset>
        get() {
            return (1L..4L).map { id -> Asset(
                assetId = id,
                thumbnailKey = "thumb_$id",
                assetType = AssetType.OBJECT,
                unityKey = "object_$id",
            ) }
        }

    @Test
    fun home_empty_light() = captureContent(darkTheme = false) { HomeContent() }

    @Test
    fun home_empty_dark() = captureContent(darkTheme = true) { HomeContent() }

    @Test
    fun home_cards_light() = captureContent(darkTheme = false) { HomeContent(cardPreviews = sampleCards) }

    @Test
    fun home_cards_dark() = captureContent(darkTheme = true) { HomeContent(cardPreviews = sampleCards) }

    @Test
    fun card_editor_light() = captureContent(darkTheme = false) { CardEditorContent(objectItems = sampleAssets) }

    @Test
    fun card_editor_dark() = captureContent(darkTheme = true) { CardEditorContent(objectItems = sampleAssets) }

    @Test
    fun card_editor_transform_light() = captureContent(darkTheme = false) { TransformContent() }

    @Test
    fun card_editor_transform_dark() = captureContent(darkTheme = true) { TransformContent() }

    @Test
    fun card_editor_text_edit_empty_light() = captureContent(darkTheme = false) { TextEditContent() }

    @Test
    fun card_editor_text_edit_empty_dark() = captureContent(darkTheme = true) { TextEditContent() }

    @Test
    fun card_editor_text_edit_light() = captureContent(darkTheme = false) { TextEditContent(textElement = TextElement()) }

    @Test
    fun card_editor_text_edit_dark() = captureContent(darkTheme = true) { TextEditContent(textElement = TextElement()) }

    @Test
    fun card_share_light() = captureContent(darkTheme = false) { CardShareContent() }

    @Test
    fun card_share_dark() = captureContent(darkTheme = true) { CardShareContent() }

    @Test
    fun setting_light() = captureContent(darkTheme = false) { SettingContent() }

    @Test
    fun setting_dark() = captureContent(darkTheme = true) { SettingContent() }

    @Test
    fun tutorial_light() = captureContent(darkTheme = false) { TutorialContent() }

    @Test
    fun tutorial_dark() = captureContent(darkTheme = true) { TutorialContent() }

    @Test
    fun objectDeleteDialog_light() = captureScreen(darkTheme = false) {
        ObjectDeleteConfirmDialog(onDeleteObject = {}, onDismiss = {})
    }

    @Test
    fun objectDeleteDialog_dark() = captureScreen(darkTheme = true) {
        ObjectDeleteConfirmDialog(onDeleteObject = {}, onDismiss = {})
    }

    @Test
    fun unsavedChangesDialog_light() = captureScreen(darkTheme = false) {
        UnsavedChangesDialog(onApplyChanges = {}, onDiscardChanges = {}, onDismiss = {})
    }

    @Test
    fun unsavedChangesDialog_dark() = captureScreen(darkTheme = true) {
        UnsavedChangesDialog(onApplyChanges = {}, onDiscardChanges = {}, onDismiss = {})
    }

    @Test
    fun uploadCancelConfirmDialog_light() = captureScreen(darkTheme = false) {
        UploadCancelConfirmDialog(onCancelUpload = {}, onDismiss = {})
    }

    @Test
    fun uploadCancelConfirmDialog_dark() = captureScreen(darkTheme = true) {
        UploadCancelConfirmDialog(onCancelUpload = {}, onDismiss = {})
    }

    private fun captureContent(darkTheme: Boolean, content: @Composable () -> Unit) {
        composeRule.setContent { ZCardTheme(darkTheme = darkTheme, content = content) }
        composeRule.onRoot().captureRoboImage()
    }

    // Dialog, BottomSheet는 별도 window에 그려지므로 화면 전체를 캡처
    // Robolectric은 non-touch mode라 Dialog 내 TextField가 자동 포커스되고, 커서 깜빡임(무한 애니메이션) 때문에
    // idle 상태가 되지 않음 → touch mode로 전환해 자동 포커스를 막은 뒤 캡처
    @OptIn(ExperimentalRoborazziApi::class)
    private fun captureScreen(darkTheme: Boolean, content: @Composable () -> Unit) {
        InstrumentationRegistry.getInstrumentation().setInTouchMode(true)
        composeRule.setContent { ZCardTheme(darkTheme = darkTheme, content = content) }
        composeRule.waitForIdle()
        captureScreenRoboImage()
    }
}
