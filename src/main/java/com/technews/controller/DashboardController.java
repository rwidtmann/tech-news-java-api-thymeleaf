package com.technews.controller;

import com.technews.exception.NoEmailException;
import com.technews.model.Comment;
import com.technews.model.Post;
import com.technews.model.User;
import com.technews.model.Vote;
import com.technews.repository.CommentRepository;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.spi.http.HttpContext;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;


    @PostMapping("/users/login")
    public void login(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) throws NoEmailException, Exception {

        User sessionUser = userRepository.findUserByEmail(user.getEmail());

        try {
            if (sessionUser.equals(null)) {

            }
        } catch (NullPointerException e) {
            response.sendRedirect("/login");
            throw new NoEmailException("No user with that email address!");
        }

        sessionUser.setLoggedIn(true);
        request.getSession().setAttribute("SESSION_USER", sessionUser);

        response.sendRedirect("/dashboard");
    }


    @GetMapping("/dashboard")
    public String getAllPosts(Model model, HttpServletRequest request) throws Exception {

        if (request.getSession(false) != null) {
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
            model.addAttribute("user", new User());
            return "login";
        }
    }


    @GetMapping("/dashboard/edit/{id}")
    public String editPost(@PathVariable int id, Model model, HttpServletRequest request) {

        if (request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

            Post returnPost = postRepository.getOne(id);
            User tempUser = userRepository.getOne(returnPost.getUserId());
            returnPost.setUserName(tempUser.getUsername());
            returnPost.setVoteCount(voteRepository.countPostByPostId(returnPost.getId()));

            List<Comment> commentList = commentRepository.findAllCommentsByPostId(returnPost.getId());

            model.addAttribute("post", returnPost);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
            model.addAttribute("commentList", commentList);
            model.addAttribute("comment", new Comment());

            return "edit-post";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }

    @PostMapping("/posts")
    public String addPost(@ModelAttribute Post post, HttpServletRequest request) {

        if (request.getSession(false) == null) {
            return "redirect:/login";
        } else {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            post.setUserId(sessionUser.getId());
            postRepository.save(post);

            return "redirect:/dashboard";
        }
    }


    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable int id, @ModelAttribute Post post, Model model, HttpServletRequest request) {

        if (request.getSession(false) == null) {
            model.addAttribute("user", new User());
            return "redirect/dashboard";
        } else {
            Post tempPost = postRepository.getOne(id);
            tempPost.setTitle(post.getTitle());
            postRepository.save(tempPost);

            return "redirect:/dashboard";
        }
    }


    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@ModelAttribute Comment comment, HttpServletRequest request, HttpServletResponse response) throws NoEmailException, Exception {

        if (comment.getCommentText().equals("") || comment.getCommentText().equals(null)) {
            response.sendRedirect("/post/" + comment.getPostId());
            throw new NoEmailException("You must enter a comment!");
        } else {
            if (request.getSession(false) != null) {
                User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
                comment.setUserId(sessionUser.getId());
                commentRepository.save(comment);

                response.sendRedirect("/post/" + comment.getPostId());
            } else {
                response.sendRedirect("/login");
            }
        }

    }


    @PostMapping("/comments/edit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCommentEditPage(@ModelAttribute Comment comment, HttpServletRequest request, HttpServletResponse response) throws NoEmailException, Exception {

        if (comment.getCommentText().equals("") || comment.getCommentText().equals(null)) {
            response.sendRedirect("/dashboard/edit/" + comment.getPostId());
            throw new NoEmailException("You must enter a comment!");
        } else {
            if (request.getSession(false) != null) {
                User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
                comment.setUserId(sessionUser.getId());
                commentRepository.save(comment);

                response.sendRedirect("/dashboard/edit/" + comment.getPostId());
            } else {
                response.sendRedirect("/login");
            }
        }

    }


    @PutMapping("/posts/upvote")
    public void addVote(@RequestBody Vote vote, HttpServletRequest request, HttpServletResponse response) {

        if (request.getSession(false) != null) {
            Post returnPost = null;

            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            vote.setUserId(sessionUser.getId());
            voteRepository.save(vote);

            returnPost = postRepository.getOne(vote.getPostId());
            returnPost.setVoteCount(voteRepository.countPostByPostId(vote.getPostId()));
        }
    }


}
