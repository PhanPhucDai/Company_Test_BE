package com.example.Compaytest.controller;

import com.example.Compaytest.dto.PostDTO;
import com.example.Compaytest.entity.Post;
import com.example.Compaytest.service.PostService;
import com.example.Compaytest.service.ServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Create Post
    @PostMapping
    public ResponseEntity<ServiceResponse<Post>> createPost(
            @RequestBody @Valid PostDTO postDTO
    ) {
        return ResponseEntity.ok(postService.createPost(postDTO));
    }

    @GetMapping("/my_post")
    public ResponseEntity<ServiceResponse<List<Post>>> getMyPosts(@RequestParam String username) {
        ServiceResponse<List<Post>> response = postService.findMyPosts(username);
        if (!response.isSuccess()) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // Get Post by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<Post>> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    // Get all Posts
    @GetMapping
    public ResponseEntity<ServiceResponse<List<Post>>> getAllPosts() {
        return ResponseEntity.ok(postService.findAll());
    }

    // Update Post
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<Post>> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid PostDTO postDTO
    ) {
        return ResponseEntity.ok(postService.updatePost(id, postDTO));
    }

    // Delete Post
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<Void>> deletePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.deletePost(id));
    }

    @GetMapping("/search")
    public List<Post> searchPosts(@RequestParam("q") String keyword) {
        return postService.searchPosts(keyword);
    }
}
