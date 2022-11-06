package com.match.matchapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/api/students")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping(path = "/getAll")
    @ResponseBody
    @CrossOrigin(origins="http://localhost:3000")
    public List<Student> fetchAllStudents() {
        return matchService.getAllStudents();
    }

    @Transactional
    @PostMapping(path = "/add")
    @CrossOrigin(origins="http://localhost:3000")
    public ResponseEntity addStudent(@RequestBody Student student){
        if(matchService.addStudent(student)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    @PostMapping(path = "/matchAdd")
    @CrossOrigin(origins="http://localhost:3000")
    public ResponseEntity addMatch(@RequestBody Matches match){
        if(matchService.addMatches(match)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/validate/{userName}/{password}")
    @CrossOrigin(origins="http://localhost:3000")
    public ResponseEntity validation(@PathVariable String userName, @PathVariable String password){
        if (matchService.validation(userName, password)){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping(path = "/getStudent/{userName}")
    @ResponseBody
    @CrossOrigin(origins="http://localhost:3000")
    public Student getStudent(@PathVariable String userName) throws JsonProcessingException {
        return matchService.getStudent(userName);
    }

    @Transactional
    @DeleteMapping(path = "/delete/{userName}")
    @CrossOrigin(origins="http://localhost:3000")
    public  ResponseEntity deleteStudent(@PathVariable String userName){
        if (matchService.deleteStudent(userName)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(path = "/matches/{userName}")
    @ResponseBody
    @CrossOrigin(origins="http://localhost:3000")
    public List<Student> matchStudent(@PathVariable String userName) throws JsonProcessingException {

        //Student studentObject= getStudent(userName);
        List<Student> students = matchService.matchStudent(userName);

        return students;

    }
    @PostMapping(path = "/update")
    @ResponseBody
    @CrossOrigin(origins="http://localhost:3000")
    public  ResponseEntity updateStudent(@RequestBody Student student){
        student.print();
        if (matchService.updateStudent(student)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(path = "areMatched/{userName1}/{userName2}")
    @CrossOrigin(origins="http://localhost:3000")
    public ResponseEntity matchExists(@PathVariable String userName1, @PathVariable String userName2){
        if (matchService.matchExists(userName1, userName2)){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

}
