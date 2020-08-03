package com.technews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technews.exception.NoEmailException;
import com.technews.model.Post;
import com.technews.model.User;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository repository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    ObjectMapper mapper;


    @GetMapping("/api/users/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        response.sendRedirect("/login");
    }


    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        List<User> userList = repository.findAll();
        for (User u : userList) {
            List<Post> postList = u.getPosts();
            for (Post p : postList) {
                p.setVoteCount(voteRepository.countPostByPostId(p.getId()));
            }
        }
        return userList;
    }


    @PostMapping("/api/users")
    public User addUser(@RequestBody User user, HttpServletResponse response) throws IOException {
        repository.save(user);
        return user;
    }


    @PutMapping("/api/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        User tempUser = repository.getOne(id);

        if (!tempUser.equals(null)) {
            user.setId(tempUser.getId());
            repository.save(user);
        }

        return user;
    }


    @DeleteMapping("/api/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        repository.deleteById(id);
    }

}
