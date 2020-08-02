package com.technews.controller;

import com.technews.exception.NoEmailException;
import com.technews.model.Comment;
import com.technews.model.User;
import com.technews.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
//@RequestMapping("/api")
public class CommentController {

    @Autowired
    CommentRepository repository;

    @GetMapping("/comments")
    public List<Comment> getAllComments()  {
            return repository.findAll();
    }


    @GetMapping("/comments/{id}")
    public Comment getComment(@PathVariable int id) {
        return repository.getOne(id);
    }


    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@ModelAttribute Comment comment, HttpServletRequest request, HttpServletResponse response) throws NoEmailException, Exception {
        String returnValue = "";

        if(request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            comment.setUserId(sessionUser.getId());
            repository.save(comment);
            response.sendRedirect("/posts/" + comment.getPostId());
        } else {
            response.sendRedirect("/login");
            //returnValue = "login";
        }

        //return returnValue;
    }


    //    @PutMapping("/updateComment")
//    public Comment updateComment(@RequestBody Comment comment) {
//        return repository.save(comment);
//    }
//
//
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable int id) {
        repository.deleteById(id);
    }
}
