package com.technews.controller;

import com.technews.model.Comment;
import com.technews.model.Post;
import com.technews.model.User;
import com.technews.repository.CommentRepository;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
//@RequestMapping("/api")
public class DashboardController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;


    @GetMapping("/dashboard")
    public String getAllPosts(Model model, HttpServletRequest request) throws Exception {

        if(request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

            Integer userId = sessionUser.getId();

            List<Post> postList = postRepository.findAllPostsByUserId(userId);
            for (Post p : postList) {
                p.setVoteCount(voteRepository.countPostByPostId(p.getId()));
                User user = userRepository.getOne(p.getUserId());
                p.setUserName(user.getUsername());
            }

            model.addAttribute("user", sessionUser);
            model.addAttribute("postList", postList);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
            model.addAttribute("post", new Post());

            return "dashboard";
        } else {
            //model.addAttribute("loggedIn", false);
            model.addAttribute("user", new User());
            return "login";
        }
    }


    @GetMapping("/dashboard/edit/{id}")
    public String editPost(@PathVariable int id, Model model, HttpServletRequest request) {

        if(request.getSession(false) != null) {
            Post returnPost = postRepository.getOne(id);
            User tempUser = userRepository.getOne(returnPost.getUserId());
            returnPost.setUserName(tempUser.getUsername());
            returnPost.setVoteCount(voteRepository.countPostByPostId(returnPost.getId()));

            List<Comment> commentList = commentRepository.findAllCommentsByPostId(returnPost.getId());

            model.addAttribute("post", returnPost);
            model.addAttribute("commentList", commentList);

            return "edit-post-main";
        } else {
            return "login-main";
        }
    }

}
