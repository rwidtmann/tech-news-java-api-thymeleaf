package com.technews.controller;

import com.technews.model.Comment;
import com.technews.model.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomepageThymeleafController {

    @GetMapping("/homepage-thymeleaf")
    public String homePageThymeleaf(Model model) {
        List<Comment> comments = new ArrayList<>();

        Comment comment1 = new Comment(1, "Comment 1", 1, 1);
        Comment comment2 = new Comment(2, "Comment 2", 1, 1);
        Comment comment3 = new Comment(3, "Comment 3", 1, 1);
        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);

        Post post = new Post();
        post.setUserId(1);
        post.setUserName("rwTest");
        post.setComments(comments);
        post.setPostUrl("http://cnn.com");
        post.setTitle("Testing CNN");
        post.setVoteCount(5);

        model.addAttribute("post", post);
        return "homepage";
    }
}
