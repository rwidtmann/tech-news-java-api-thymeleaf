package com.technews.controller;

import com.technews.exception.NoEmailException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
//@RequestMapping("/api")
public class HomepageController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    CommentRepository commentRepository;


    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getSession(false) != null) {
            response.sendRedirect("/");
            return "";
        }

        model.addAttribute("user", new User());
        return "login";
    }


    @GetMapping("/")
    public String getAllPosts(Model model, HttpServletRequest request) {
        User sessionUser = new User();

        if (request.getSession(false) != null) {
            sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        } else {
            model.addAttribute("loggedIn", false);
        }


        List<Post> postList = postRepository.findAll();
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countPostByPostId(p.getId()));
            User user = userRepository.getOne(p.getUserId());
            p.setUserName(user.getUsername());
        }

        model.addAttribute("postList", postList);
        model.addAttribute("loggedIn", sessionUser.isLoggedIn());

        return "homepage";
    }


    @GetMapping("/post/{id}")
    public String getPostById(@PathVariable int id, Model model, HttpServletRequest request) throws NoEmailException {

        if (request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

            Post post = postRepository.getOne(id);
            post.setVoteCount(voteRepository.countPostByPostId(post.getId()));

            User postUser = userRepository.getOne(post.getUserId());
            post.setUserName(postUser.getUsername());

            List<Comment> commentList = commentRepository.findAllCommentsByPostId(post.getId());

            model.addAttribute("post", post);
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
            model.addAttribute("commentList", commentList);

            return "single-post-main";      // rjw testing
        } else {
            return "login-main";     // rjw
        }

    }


    // rjw - this needs to be removed. Commenting for now to see what will break
    @GetMapping("/homePage/loggedIn")
    public String loggedInHomePage(Model model, HttpServletRequest request) {

//        List<Post> postList = postRepository.findAll();
//        for (Post p : postList) {
//            p.setVoteCount(voteRepository.countPostByPostId(p.getId()));
//            User user = userRepository.getOne(p.getUserId());
//            p.setUserName(user.getUsername());
//        }
//
//        model.addAttribute("postList", postList);
//
//        request.getSession().setAttribute("LOGIN_SESSION", postList);
//
//        //request.getSession().invalidate();
//        if (!request.getSession().getAttribute("LOGIN_SESSION").equals(null)) {
//            System.out.println("Session Varialbe is present");
//        } else {
//            System.out.println("Session variable IS NOT present");
//        }
//
//        return "logged-in-homepage-main";
        return "";
    }


//    @GetMapping("/homepage")
//    public String getHomepage(Model model) {
//        Post post = new Post();
//        post.setId(55);
//        post.setPostUrl("https://handlebarsjs.com/guide/");
//        post.setTitle("Handlebars Docs");
//        post.setVoteCount(10);
//        post.setComments(Arrays.asList(new Comment(), new Comment()));
//
//        User user = new User();
//        user.setUsername("test_user");
//
//        model.addAttribute("message", "This is the test");
//        model.addAttribute("message2", "Message 2 from homepage endpoint");
//        model.addAttribute("post", post);
//        model.addAttribute("user", user);
//        return "homepage-main";
//    }


//    @GetMapping("/homeRoutes")
//    public String greetingForm(Model model) {
//        //model.addAttribute("user", new User());
//        return "homepage";
//    }

//    @PostMapping("/homeRoutes")
//    public String greetingSubmit(@ModelAttribute User user) {
//        return "result";
//    }


//    @GetMapping("/mustache")
//    public String mustacheDemo(Model model) {
//        //model.addAttribute("user", new User());
//        model.addAttribute("message", "This is the test");
//        model.addAttribute("message2", "Changing message 2");
//
//        return "maybe-main";
//    }


}
