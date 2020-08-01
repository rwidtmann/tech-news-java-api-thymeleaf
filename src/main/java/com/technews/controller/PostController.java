package com.technews.controller;

import com.technews.model.Post;
import com.technews.model.User;
import com.technews.model.Vote;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
//@RequestMapping("/api")
public class PostController {

    @Autowired
    PostRepository repository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/posts")
    public List<Post> getAllPosts(Model model) {
        List<Post> postList = repository.findAll();
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countPostByPostId(p.getId()));
        }
        return postList;
    }


    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Integer id, HttpServletRequest request) {
        String returnValue = "";

        if(request.getSession(false) != null) {
            Post returnPost = repository.getOne(id);
            User tempUser = userRepository.getOne(returnPost.getUserId());
            returnPost.setUserName(tempUser.getUsername());
            returnPost.setVoteCount(voteRepository.countPostByPostId(returnPost.getId()));
            returnValue = "";
        } else {
            returnValue = "login-main";
        }

        return returnValue;
    }


    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public String addPost(@ModelAttribute Post post, HttpServletRequest request) {
        String returnValue = "";

        if(request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            post.setUserId(sessionUser.getId());
            repository.save(post);

            returnValue = "";
        } else {
            returnValue = "login-main";
        }

        return returnValue;
    }


    @PutMapping("/posts/{id}")
    public String updatePost(@PathVariable int id, @RequestBody Post post, HttpServletRequest request) {
        String returnValue = "";

        if(request.getSession(false) != null) {
            Post tempPost = repository.getOne(id);
            tempPost.setTitle(post.getTitle());
            repository.save(tempPost);

            returnValue = "";
        } else {
            returnValue = "login-main";
        }

        return returnValue;
    }


    @PutMapping("/posts/upvote")
    public String addVote(@RequestBody Vote vote, HttpServletRequest request) {
        String returnValue = "";

        if(request.getSession(false) != null) {
            Post returnPost = null;

            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            vote.setUserId(sessionUser.getId());
            voteRepository.save(vote);

            returnPost = repository.getOne(vote.getPostId());
            returnPost.setVoteCount(voteRepository.countPostByPostId(vote.getPostId()));

            returnValue = "";
        } else {
            returnValue = "login";
        }

        return returnValue;
    }


    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int id) {
        repository.deleteById(id);
    }
}
