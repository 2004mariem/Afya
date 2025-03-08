package com.example.afya.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.afya.domain.usecase.AddPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val addPostUseCase: AddPostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddPostState())
    val state: StateFlow<AddPostState> = _state.asStateFlow()

    fun onEvent(event: AddPostEvent) {
        when (event) {
            is AddPostEvent.TitleChanged -> _state.update { it.copy(title = event.title) }
            is AddPostEvent.DrugNameChanged -> _state.update { it.copy(drugName = event.drugName) }
            is AddPostEvent.ContentChanged -> _state.update { it.copy(content = event.content) }
            is AddPostEvent.LocationChanged -> _state.update { it.copy(location = event.location) }
            is AddPostEvent.ImageUrlChanged -> _state.update { it.copy(imageUrl = event.url) }
            is AddPostEvent.PostTypeSelected -> _state.update { it.copy(postType = event.postType) }
            is AddPostEvent.Submit -> submitPost()
        }
    }

    private fun submitPost() {
        val currentState = _state.value

        // التحقق من صحة البيانات
        if (currentState.title.isBlank() || currentState.drugName.isBlank() || currentState.content.isBlank() || currentState.location.isBlank()) {
            _state.update { it.copy(error = "جميع الحقول مطلوبة!") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            try {
                addPostUseCase(
                    Post(
                        title = currentState.title,
                        drugName = currentState.drugName,
                        content = currentState.content,
                        location = currentState.location,
                        imageUrl = currentState.imageUrl,
                        postType = currentState.postType
                    )
                )
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "فشل في إرسال المنشور!") }
            }
        
    }
}
