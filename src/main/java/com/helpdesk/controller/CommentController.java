package com.helpdesk.controller;

import com.helpdesk.dto.CommentRequest;
import com.helpdesk.entity.Comment;
import com.helpdesk.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.getCommentsByTicket(ticketId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<Comment> addComment(@PathVariable Long ticketId,
                                               @Valid @RequestBody CommentRequest request,
                                               Authentication authentication) {
        Comment comment = commentService.addComment(ticketId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}
