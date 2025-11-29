package com.chuwa.redbook;

import com.chuwa.redbook.payload.PostDto;
import com.chuwa.redbook.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedbookApplicationTests {

	@Autowired
	private PostServiceImpl postService;

    @Test
    @Transactional
    @Rollback
    public void testGetAllPosts() {
        // First create a test post to ensure there's at least one post to retrieve
        PostDto postDto = new PostDto();
        postDto.setTitle("Test Post Title");
        postDto.setDescription("Test Post Description");
        postDto.setContent("Test Post Content");

        postService.createPost(postDto);

        // Now get all posts
        List<PostDto> posts = postService.getAllPost();

        assertNotNull(posts);
        assertTrue(posts.size() > 0);
    }

    @Test
    @Transactional
    @Rollback
    public void testCreatePost() {
        // Create a test post with all required fields
        PostDto postDto = new PostDto();
        postDto.setTitle("Test Post Title");
        postDto.setDescription("Test Post Description");
        postDto.setContent("Test Post Content");

        // Use @Transactional to rollback after test
        // Or mock the repository instead of using the actual database
        PostDto savedPost = postService.createPost(postDto);

        assertNotNull(savedPost);
        assertNotNull(savedPost.getId());
        assertEquals("Test Post Title", savedPost.getTitle());
    }

}
