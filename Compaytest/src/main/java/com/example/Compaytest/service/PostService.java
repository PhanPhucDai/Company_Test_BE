package com.example.Compaytest.service;

import com.example.Compaytest.dto.PostDTO;
import com.example.Compaytest.entity.Post;
import com.example.Compaytest.entity.User;
import com.example.Compaytest.exception.NotificationCode;
import com.example.Compaytest.repository.PostRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepo;
    private final UserService userService; //  tìm User theo username

    @Transactional
    public ServiceResponse<Post> createPost(PostDTO postDTO) {
        try {
            // Lấy User author từ username trong DTO
            ServiceResponse<User> userResp = userService.findByUsername(postDTO.getUsername());
            if (!userResp.isSuccess() || userResp.getData() == null) {
                return ServiceResponse.error(NotificationCode.USER_NOT_FOUND);
            }
            User author = userResp.getData();

            Post post = Post.builder()
                    .title(postDTO.getTitle())
                    .content(postDTO.getContent())
                    .author(author)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Post saved = postRepo.save(post);
            return ServiceResponse.success(NotificationCode.POST_CREATE_SUCCESS, saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse<Post> findById(Long id) {
        try {
            Optional<Post> postOpt = postRepo.findById(id);
            if (postOpt.isPresent()) {
                return ServiceResponse.success(NotificationCode.POST_FOUND, postOpt.get());
            } else {
                return ServiceResponse.error(NotificationCode.POST_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse<List<Post>> findAll() {
        try {
            List<Post> posts = postRepo.findAll();
            return ServiceResponse.success(NotificationCode.POST_FOUND, posts);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ServiceResponse<Post> updatePost(Long id, PostDTO postDTO) {
        try {
            Optional<Post> postOpt = postRepo.findById(id);
            if (postOpt.isEmpty()) {
                return ServiceResponse.error(NotificationCode.POST_NOT_FOUND);
            }

            Post post = postOpt.get();
            post.setTitle(postDTO.getTitle());
            post.setContent(postDTO.getContent());
            post.setUpdatedAt(LocalDateTime.now());
            //  update author theo DTO username
            if (postDTO.getUsername() != null && !postDTO.getUsername().isEmpty()) {
                ServiceResponse<User> userResp = userService.findByUsername(postDTO.getUsername());
                if (!userResp.isSuccess() || userResp.getData() == null) {
                    return ServiceResponse.error(NotificationCode.USER_NOT_FOUND);
                }
                post.setAuthor(userResp.getData());
            }

            Post saved = postRepo.save(post);
            return ServiceResponse.success(NotificationCode.POST_UPDATE_SUCCESS, saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ServiceResponse<Void> deletePost(Long id) {
        try {
            if (!postRepo.existsById(id)) {
                return ServiceResponse.error(NotificationCode.POST_NOT_FOUND);
            }
            postRepo.deleteById(id);
            return ServiceResponse.success(NotificationCode.POST_DELETE_SUCCESS, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ServiceResponse<List<Post>> findMyPosts(String username) {
        try {
            // Lấy User từ username
            ServiceResponse<User> userResp = userService.findByUsername(username);
            if (!userResp.isSuccess() || userResp.getData() == null) {
                return ServiceResponse.error(NotificationCode.USER_NOT_FOUND);
            }
            User author = userResp.getData();

            // Lấy tất cả post của user
            List<Post> posts = postRepo.findByAuthor(author);
            return ServiceResponse.success(NotificationCode.POST_FOUND, posts);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return postRepo.findAll(); // nếu không nhập keyword thì trả về tất cả
        }
        return postRepo.searchPosts(keyword);
    }
}


