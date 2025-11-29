package com.chuwa.redbook.service.impl;

import com.chuwa.redbook.dao.CommentRepository;
import com.chuwa.redbook.dao.PostRepository;
import com.chuwa.redbook.entity.Comment;
import com.chuwa.redbook.entity.Post;
import com.chuwa.redbook.exception.BlogAPIException;
import com.chuwa.redbook.exception.ResourceNotFoundException;
import com.chuwa.redbook.payload.CommentDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for CommentServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImplTest.class);

    @Mock
    private CommentRepository commentRepositoryMock;

    @Mock
    private PostRepository postRepositoryMock;

    @Mock
    private ModelMapper modelMapperMock;

    @InjectMocks
    private CommentServiceImpl commentService;

    // Test data
    private CommentDto commentDto;
    private Comment comment;
    private Post post;
    private Long postId = 1L;
    private Long commentId = 1L;

    @BeforeAll
    static void beforeAll() {
        logger.info("START CommentServiceImpl tests");
    }

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for each test");

        // Initialize Post
        post = new Post();
        post.setId(postId);
        post.setTitle("Test Post");
        post.setDescription("Test Description");
        post.setContent("Test Content");

        // Initialize Comment
        comment = new Comment();
        comment.setId(commentId);
        comment.setName("Test User");
        comment.setEmail("test@example.com");
        comment.setBody("Test Comment Body");
        comment.setPost(post);

        // Initialize CommentDto
        commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setName("Test User");
        commentDto.setEmail("test@example.com");
        commentDto.setBody("Test Comment Body");
    }

    @Test
    void testCreateComment() {
        // Define behaviors
        Mockito.when(modelMapperMock.map(ArgumentMatchers.any(CommentDto.class), ArgumentMatchers.eq(Comment.class)))
                .thenReturn(comment);
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentRepositoryMock.save(ArgumentMatchers.any(Comment.class)))
                .thenReturn(comment);
        Mockito.when(modelMapperMock.map(ArgumentMatchers.any(Comment.class), ArgumentMatchers.eq(CommentDto.class)))
                .thenReturn(commentDto);

        // Execute
        CommentDto result = commentService.createComment(postId, commentDto);

        // Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(commentDto.getName(), result.getName());
        Assertions.assertEquals(commentDto.getEmail(), result.getEmail());
        Assertions.assertEquals(commentDto.getBody(), result.getBody());

        // Verify interactions
        Mockito.verify(postRepositoryMock).findById(postId);
        Mockito.verify(commentRepositoryMock).save(comment);
    }

    @Test
    void testCreateComment_PostNotFound() {
        // Define behaviors - post not found
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new ResourceNotFoundException("Post", "id", postId));

        // Execute and assert exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(postId, commentDto);
        });
    }

    @Test
    void testGetCommentsByPostId() {
        // Setup test data
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        // Define behaviors
        Mockito.when(commentRepositoryMock.findByPostId(ArgumentMatchers.anyLong()))
                .thenReturn(comments);
        Mockito.when(modelMapperMock.map(ArgumentMatchers.any(Comment.class), ArgumentMatchers.eq(CommentDto.class)))
                .thenReturn(commentDto);

        // Execute
        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        // Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(commentDto.getName(), result.get(0).getName());
        Assertions.assertEquals(commentDto.getEmail(), result.get(0).getEmail());
        Assertions.assertEquals(commentDto.getBody(), result.get(0).getBody());
    }

    @Test
    void testGetCommentById() {
        // Define behaviors
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(comment));
        Mockito.when(modelMapperMock.map(ArgumentMatchers.any(Comment.class), ArgumentMatchers.eq(CommentDto.class)))
                .thenReturn(commentDto);

        // Execute
        CommentDto result = commentService.getCommentById(postId, commentId);

        // Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(commentDto.getName(), result.getName());
        Assertions.assertEquals(commentDto.getEmail(), result.getEmail());
        Assertions.assertEquals(commentDto.getBody(), result.getBody());
    }

    @Test
    void testGetCommentById_PostNotFound() {
        // Define behaviors - post not found
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new ResourceNotFoundException("Post", "id", postId));

        // Execute and assert exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getCommentById(postId, commentId);
        });
    }

    @Test
    void testGetCommentById_CommentNotFound() {
        // Define behaviors - post found but comment not found
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new ResourceNotFoundException("Comment", "id", commentId));

        // Execute and assert exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getCommentById(postId, commentId);
        });
    }

    @Test
    void testGetCommentById_CommentNotBelongToPost() {
        // Create a different post
        Post differentPost = new Post();
        differentPost.setId(2L);

        // Create a comment belonging to the different post
        Comment commentWithDifferentPost = new Comment();
        commentWithDifferentPost.setId(commentId);
        commentWithDifferentPost.setPost(differentPost);

        // Define behaviors
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post)); // Return original post with ID 1
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(commentWithDifferentPost)); // Return comment with post ID 2

        // Execute and assert exception
        BlogAPIException exception = Assertions.assertThrows(BlogAPIException.class, () -> {
            commentService.getCommentById(postId, commentId);
        });

        // Verify exception details
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        Assertions.assertEquals("Comment does not belong to post", exception.getMessage());
    }

    @Test
    void testUpdateComment() {
        // Create a mock Comment
        Comment mockComment = Mockito.mock(Comment.class);

        // Configure the mock to return the post when getPost() is called
        Mockito.when(mockComment.getPost()).thenReturn(post);

        // Define behaviors
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(mockComment));
        Mockito.when(commentRepositoryMock.save(ArgumentMatchers.any(Comment.class)))
                .thenReturn(mockComment);
        Mockito.when(modelMapperMock.map(ArgumentMatchers.any(Comment.class), ArgumentMatchers.eq(CommentDto.class)))
                .thenReturn(commentDto);

        // Create updated comment DTO
        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setId(commentId);
        updatedCommentDto.setName("Updated Name");
        updatedCommentDto.setEmail("updated@example.com");
        updatedCommentDto.setBody("Updated body");

        // Execute
        CommentDto result = commentService.updateComment(postId, commentId, updatedCommentDto);

        // Verify
        Assertions.assertNotNull(result);

        // Verify the comment was updated with new values
        Mockito.verify(mockComment).setName(updatedCommentDto.getName());
        Mockito.verify(mockComment).setEmail(updatedCommentDto.getEmail());
        Mockito.verify(mockComment).setBody(updatedCommentDto.getBody());

        // Verify repository save was called
        Mockito.verify(commentRepositoryMock).save(mockComment);
    }

    @Test
    void testUpdateComment_PostNotFound() {
        // Define behaviors - post not found
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new ResourceNotFoundException("Post", "id", postId));

        // Execute and assert exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(postId, commentId, commentDto);
        });
    }

    @Test
    void testUpdateComment_CommentNotFound() {
        // Define behaviors - post found but comment not found
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new ResourceNotFoundException("Comment", "id", commentId));

        // Execute and assert exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(postId, commentId, commentDto);
        });
    }

    @Test
    void testUpdateComment_CommentNotBelongToPost() {
        // Create a different post
        Post differentPost = new Post();
        differentPost.setId(2L);

        // Create a comment belonging to the different post
        Comment commentWithDifferentPost = new Comment();
        commentWithDifferentPost.setId(commentId);
        commentWithDifferentPost.setPost(differentPost);

        // Define behaviors
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post)); // Return original post with ID 1
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(commentWithDifferentPost)); // Return comment with post ID 2

        // Execute and assert exception
        BlogAPIException exception = Assertions.assertThrows(BlogAPIException.class, () -> {
            commentService.updateComment(postId, commentId, commentDto);
        });

        // Verify exception details
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        Assertions.assertEquals("Comment does not belong to post", exception.getMessage());
    }

    @Test
    void testDeleteComment() {
        // Define behaviors
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(comment));
        Mockito.doNothing().when(commentRepositoryMock).delete(ArgumentMatchers.any(Comment.class));

        // Execute
        commentService.deleteComment(postId, commentId);

        // Verify repository delete was called
        Mockito.verify(commentRepositoryMock).delete(comment);
    }

    @Test
    void testDeleteComment_PostNotFound() {
        // Define behaviors - post not found
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new ResourceNotFoundException("Post", "id", postId));

        // Execute and assert exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(postId, commentId);
        });
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        // Define behaviors - post found but comment not found
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new ResourceNotFoundException("Comment", "id", commentId));

        // Execute and assert exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(postId, commentId);
        });
    }

    @Test
    void testDeleteComment_CommentNotBelongToPost() {
        // Create a different post
        Post differentPost = new Post();
        differentPost.setId(2L);

        // Create a comment belonging to the different post
        Comment commentWithDifferentPost = new Comment();
        commentWithDifferentPost.setId(commentId);
        commentWithDifferentPost.setPost(differentPost);

        // Define behaviors
        Mockito.when(postRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(post)); // Return original post with ID 1
        Mockito.when(commentRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(commentWithDifferentPost)); // Return comment with post ID 2

        // Execute and assert exception
        BlogAPIException exception = Assertions.assertThrows(BlogAPIException.class, () -> {
            commentService.deleteComment(postId, commentId);
        });

        // Verify exception details
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        Assertions.assertEquals("Comment does not belong to post", exception.getMessage());
    }

    @Test
    void testCommentServiceMapperUtil() {
        // This is a static method test
        // Create a real ModelMapper for this test (since we're testing the static utility method)
        Comment testComment = new Comment();
        testComment.setId(100L);
        testComment.setName("Static Test");
        testComment.setEmail("static@test.com");
        testComment.setBody("Static test body");

        // Execute
        CommentDto result = CommentServiceImpl.commentServiceMapperUtil(testComment);

        // Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testComment.getId(), result.getId());
        Assertions.assertEquals(testComment.getName(), result.getName());
        Assertions.assertEquals(testComment.getEmail(), result.getEmail());
        Assertions.assertEquals(testComment.getBody(), result.getBody());
    }
}