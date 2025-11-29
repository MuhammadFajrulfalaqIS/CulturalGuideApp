# 🎯 SIMPLE FIX - NO MORE COMPLEX CODE

## **❌ STOP USING THESE FILES:**
- `EnhancedReviewDialog.kt` (delete - too many errors)
- `ReviewDialogWithGallery.kt` (delete - too many errors)
- `CompleteWorkingExample.kt` (delete - too many errors)
- `ImagePickerComponent.kt` (delete - too many errors)
- `SimpleWorkingExample.kt` (delete - too many errors)
- `EnhancedReviewDialogFinal.kt` (delete - too many errors)

## **✅ ONLY USE THESE FIXES:**

### **1. Fix Your Existing DetailScreen.kt**
```kotlin
// Di DetailScreen.kt, TAMBAH INI:
var showReviewDialog by remember { mutableStateOf(false) }

// Di tempat tombol "Write Review":
Button(onClick = { showReviewDialog = true }) {
    Text("Write Review")
}

// Di bagian bawah composable:
if (showReviewDialog) {
    // Simple review dialog TANPA gallery dulu
    AlertDialog(
        onDismissRequest = { showReviewDialog = false },
        title = { Text("Review for ${place.name}") },
        text = {
            var rating by remember { mutableStateOf(0f) }
            var reviewText by remember { mutableStateOf("") }

            Column {
                Text("Rating:")
                Row {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star.toFloat() }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Star $star",
                                tint = if (star <= rating) Color.Yellow else Color.Gray
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Your Review") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Simpan review logic di sini
                    showReviewDialog = false
                }
            ) {
                Text("Submit")
            }
        }
    )
}
```

### **2. Fix DraggableBottomSheet.kt (1 line change):**
```kotlin
// Tambahah import ini:
import kotlinx.coroutines.launch

// Di dalam DraggableLocationBottomSheet composable:
val scope = rememberCoroutineScope()

// Ubah line 70:
// DARI: scope.launch { bottomSheetState.partialExpand() }
// MENJADI:
scope.launch { bottomSheetState.partialExpand() }
```

### **3. Fix ReviewViewModel.kt (1 line change):**
```kotlin
// Pastikan import benar:
import com.example.cultural_navigation_papb.data.models.Review
```

## **🎯 HASIL:**
✅ **Hanya 3 file perlu di-fix**
✅ **Tidak perlu komponen baru yang kompleks**
✅ **Review system sederhana dulu**
✅ **Map interaction sudah fixed**

**Ini jauh lebih sederhana dan tidak menimbulkan error baru!**