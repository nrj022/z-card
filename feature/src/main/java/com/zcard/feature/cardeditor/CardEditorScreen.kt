package com.zcard.feature.cardeditor

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.feature.cardeditor.dialog.CardInfoDialog
import com.zcard.feature.cardeditor.dialog.ObjectDeleteConfirmDialog
import com.zcard.feature.cardeditor.dialog.SetCardTitleDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.Asset
import com.zcard.feature.cardeditor.component.BaseTabs
import com.zcard.feature.cardeditor.util.toBase62
import com.zcard.feature.cardeditor.util.getObjectThumbByKey
import com.zcard.designsystem.util.DrawableResProvider.getBgThumbByKey
import com.zcard.feature.R
import com.zcard.feature.cardeditor.component.BaseTabItem
import com.zcard.feature.cardeditor.dialog.UploadCancelConfirmDialog
import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerType

private const val COLUMNS = 4

@Composable
fun CardEditorScreen(viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.cardEditorState.collectAsStateWithLifecycle()

    CardEditorContent(
        selectedTab = state.selectedTab,
        objectItems = state.objects,
        backgroundItems = state.backgrounds,
        spawnedObjects = state.spawnedObjects,
        selectedSpawnedObject = state.selectedSpawnedObjectId,
        selectedBackground = state.selectedBackgroundId,
        loadingObjectIds = state.loadingObjectIds,
        onTabSelected = { viewModel.onIntent(CardEditorIntent.ChangeTab(it)) },
        onAddTextClicked = { viewModel.onIntent(CardEditorIntent.EnterTextMode) },
        onObjectClicked = { viewModel.onIntent(CardEditorIntent.CreateObject(it)) },
        onSpawnedObjectClicked = { viewModel.onIntent(CardEditorIntent.SelectSpawnedObject(it)) },
        onBackgroundClicked = { viewModel.onIntent(CardEditorIntent.ChangeBackground(it)) }
    )

    when(state.dialogState) {
        CardEditorState.DialogState.NONE -> {}
        CardEditorState.DialogState.DELETE_CONFIRM -> {
            ObjectDeleteConfirmDialog(
                onDeleteObject = { viewModel.onIntent(CardEditorIntent.DeleteSpawnedObject) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(CardEditorState.DialogState.NONE)) }
            )
        }
        CardEditorState.DialogState.UPLOAD_CANCEL_CONFIRM -> {
            UploadCancelConfirmDialog(
                onCancelUpload = { viewModel.onIntent(CardEditorIntent.CancelUpload) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(CardEditorState.DialogState.NONE)) }
            )
        }
        CardEditorState.DialogState.CARD_LINK_DETAIL -> {
            CardInfoDialog(
                cardTitle = state.cardTitle,
                isTitleChanged = state.isTitleChanged,
                onTitleChange = { viewModel.onIntent(CardEditorIntent.ChangeTitle(it)) },
                onTitleSave = { viewModel.onIntent(CardEditorIntent.SaveTitle) },
                onViewLink = { viewModel.onIntent(CardEditorIntent.NavigateToCardShare) },
                onDismiss = {
                    viewModel.onIntent(CardEditorIntent.ResetTitle)
                    viewModel.onIntent(CardEditorIntent.ChangeDialogState(CardEditorState.DialogState.NONE))
                }
            )
        }
        CardEditorState.DialogState.SET_CARD_TITLE -> {
            SetCardTitleDialog(
                cardTitle = state.cardTitle,
                onTitleChange = { viewModel.onIntent(CardEditorIntent.ChangeTitle(it)) },
                onGenerateCard = { viewModel.onIntent(CardEditorIntent.ExportGlbAndUpload) },
                onDismiss = {
                    viewModel.onIntent(CardEditorIntent.ResetTitle)
                    viewModel.onIntent(CardEditorIntent.ChangeDialogState(CardEditorState.DialogState.NONE))
                }
            )
        }
    }
}

@Composable
internal fun CardEditorContent(
    modifier: Modifier = Modifier,
    selectedTab: CardEditorState.AssetBrowserTab = CardEditorState.AssetBrowserTab.OBJECTS,
    objectItems: List<Asset> = emptyList(),
    backgroundItems: List<Asset> = emptyList(),
    spawnedObjects: List<CardElementWithAssetKeys> = emptyList(),
    selectedSpawnedObject: Long? = null,
    selectedBackground: Long = 1,
    loadingObjectIds: Set<Long> = emptySet(),
    onTabSelected: (CardEditorState.AssetBrowserTab) -> Unit = {},
    onAddTextClicked: () -> Unit = {},
    onObjectClicked: (Asset) -> Unit = {},
    onSpawnedObjectClicked: (CardElementWithAssetKeys) -> Unit = {},
    onBackgroundClicked: (Long) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
    ) {
        // "3D Object", "Background" 토글 및 "NEXT" 버튼이 있는 헤더
        ControlHeader(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddTextClicked = onAddTextClicked
        )

        if (selectedTab == CardEditorState.AssetBrowserTab.OBJECTS) {
            if(spawnedObjects.isNotEmpty()) {
                SpawnedObjectRow(
                    elements = spawnedObjects,
                    selectedItemIndex = selectedSpawnedObject,
                    onItemClicked = { onSpawnedObjectClicked(it) },
                    isLoading = { id -> loadingObjectIds.contains(id) }
                )
            }
            ObjectClickableGrid(
                assets = objectItems,
                onItemClicked = { onObjectClicked(it) }
            )
        } else {
            BackgroundSelectableGrid(
                assets = backgroundItems,
                selectedItemIndex = selectedBackground,
                onItemClicked = { onBackgroundClicked(it) }
            )
        }
    }
}

@Composable
private fun ControlHeader(
    selectedTab: CardEditorState.AssetBrowserTab,
    onTabSelected: (CardEditorState.AssetBrowserTab) -> Unit,
    onAddTextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // "3D Object", "Background" 토글 버튼 그룹
        BaseTabs(
            tabs = CardEditorState.AssetBrowserTab.entries.map { BaseTabItem(it.name, it.resId) },
            selectedTabId = selectedTab.name,
            onTabSelected = { onTabSelected(CardEditorState.AssetBrowserTab.valueOf(it)) }
        )

        // "Add Text >" 버튼
        Row(
            modifier = Modifier.clickable(onClick = onAddTextClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.editor_button_add_text),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SpawnedObjectRow(
    elements: List<CardElementWithAssetKeys>,
    selectedItemIndex: Long?,
    onItemClicked: (CardElementWithAssetKeys) -> Unit,
    isLoading: (Long) -> Boolean
) {
    val context = LocalContext.current

    Column {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp),
            text = stringResource(R.string.editor_title_spawned_objects_list),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item { Box(Modifier.size(8.dp)) }
            items(items = elements, key = { it.cardElement.elementId }) { element ->
                val isLoading = isLoading(element.cardElement.elementId)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            2.dp,
                            if (element.cardElement.elementId == selectedItemIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(!isLoading) { onItemClicked(element) }
                ) {
                    Image(
                        painter = painterResource(id = getObjectThumbByKey(context, element.thumbnailKey)),
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(R.string.editor_cd_asset),
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.1f))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = element.cardElement.elementId.toBase62(),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.White.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF2F2F2F),
                                strokeWidth = 2.dp,
                            )
                        }
                    }
                }
            }
            item { Box(Modifier.size(8.dp)) }
        }
    }
}

@Composable
private fun ObjectClickableGrid(assets: List<Asset>, onItemClicked: (Asset) -> Unit) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(assets, key = { it.assetId }) { asset ->
            Image(
                painter = painterResource(id = getObjectThumbByKey(context, asset.thumbnailKey)),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.editor_cd_asset),
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .clickable { onItemClicked(asset) }
            )
        }
    }
}

@Composable
private fun BackgroundSelectableGrid(assets: List<Asset>, selectedItemIndex: Long, onItemClicked: (Long) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            ColorPickerButton()
        }
        items(items = assets, key = { it.assetId }) { asset ->
            Image(
                painter = painterResource(id = getBgThumbByKey(asset.thumbnailKey)),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.editor_cd_asset),
                modifier = Modifier
                    .aspectRatio(1f) // 1:1 비율 유지
                    .border(
                        2.dp,
                        if (asset.assetId == selectedItemIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onItemClicked(asset.assetId) }
            )
        }
    }
}

@Composable
fun ColorPickerButton(
    modifier: Modifier = Modifier,
    isClicked: Boolean = false,
    selectedColor: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // 1:1 비율 유지
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                2.dp,
                if (isClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(16.dp)
            )
            .drawBehind {
                drawRect(
                    Brush.sweepGradient(
                        listOf(
                            Color.Red, Color.Yellow, Color.Green, Color.Cyan,
                            Color.Blue, Color.Magenta, Color.Red
                        )
                    )
                )
                drawRect(
                    Brush.radialGradient(
                        listOf(Color.White, Color.Transparent),
                        radius = size.minDimension / 1.6f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.5f)
                .aspectRatio(1f)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = Color(0XFF2F2F2F),
                    spotColor =  Color(0XFF2F2F2F)
                )
                .background(color = selectedColor)
                .clip(CircleShape)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    onColorChange: (Color) -> Unit = {},
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    // 다이얼로그를 연 시점의 색으로만 초기화 (color는 선택 중에 계속 바뀌므로 고정)
    val initialColor = remember { color }

    AlertDialog(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 색상 링 + 채도·밝기 슬라이더 (배경색은 불투명만 지원하므로 알파 바는 숨김)
                ColorPicker(
                    type = ColorPickerType.Ring(
                        ringWidth = 16.dp,
                        showAlphaBar = false,
                        initialColor = initialColor
                    ),
                    onPickedColor = onColorChange
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "#%06X".format(color.toArgb() and 0xFFFFFF),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.common_button_select), style = MaterialTheme.typography.labelSmall)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.common_button_cancel), style = MaterialTheme.typography.labelSmall)
            }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
@Preview(
    name = "Light Mode",
    showBackground = true,
    showSystemUi = true,
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
private fun CardCreationScreenPreview() {
    ZCardTheme {
        CardEditorContent(selectedTab = CardEditorState.AssetBrowserTab.BACKGROUND)
    }
}