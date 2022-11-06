package com.match.matchapi;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {

    @Autowired
    private final StudentRepository studentRepository;

    @Autowired
    private final MatchRepository matchRepository;

    @Autowired
    private final MongoTemplate mongoTemplate;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public boolean validation(String username, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(username).andOperator(
                Criteria.where("password").is(password)
        ));

        List<Student> students = mongoTemplate.find(query, Student.class);

        if (students.isEmpty()){
            return false;
        }

        return students.size() == 1;
    }

    public boolean addStudent(Student student) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(student.getUserName()));
        List<Student> students = mongoTemplate.find(query, Student.class);

        if (students.size() > 0){
            return false;
        }
        studentRepository.insert(student);
        return true;
    }

    public boolean deleteStudent(String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        List<Student> students = mongoTemplate.find(query, Student.class);
        if(students == null) {
            return false;
        }

            studentRepository.deleteById(students.get(0).getId());
            return true;

    }

    public Student getStudent(String userName) {
        return mongoTemplate.findById(userName, Student.class);
    }

    public boolean updateStudent(Student student) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(student.getId()));
        List<Student> students = mongoTemplate.find(query, Student.class);
        if(students == null || students.size()<=0){
            return false;
        }

        student.setUserName(students.get(0).getUserName());

        if(student.getCourse() == null){
            student.setCourse(students.get(0).getCourse());
        }
        if(student.getCampus() == null){
            student.setCampus(students.get(0).getCampus());
        }
        if(student.getFirstName() == null){
            student.setFirstName(students.get(0).getFirstName());
        }
        if(student.getLastName() == null){
            student.setLastName(students.get(0).getLastName());
        }
        if(student.getBio() == null){
            student.setBio(students.get(0).getBio());
        }
        if(student.getMajor() == null){
            student.setMajor(students.get(0).getMajor());
        }
        if(student.getPronouns() == null){
            student.setPronouns(students.get(0).getPronouns());
        }
        if(student.getPassword() == null){
            student.setPassword(students.get(0).getPassword());
        }
        if(student.getYear() == null){
            student.setYear(students.get(0).getYear());
        }
        if(student.getGenderPreference() == null){
            student.setGenderPreference(students.get(0).getGenderPreference());
        }

        studentRepository.deleteById(student.getId());
        studentRepository.insert(student);
        return true;

    }

    private void merge(List<Integer> points,List<Student> students,int l,int m, int r) {


        /* Create temp arrays */
        int Lpoints[] = new int[m - l + 3];
        int Rpoints[] = new int[r - m + 3];

        Student Lstudents[] = new Student[m - l + 3];
        Student Rstudents[] = new Student[r - m + 3];

        /*Copy data to temp arrays*/
        for (int i = l; i <= m; i++) {
            Lpoints[i - l] = points.get(i);
            Lstudents[i - l] = students.get(i);
        }
        for (int j = m+1; j <= r; j++) {
            Rpoints[j - (m + 1)] = points.get(j);
            Rstudents[j - (m + 1)] = students.get(j);
        }

        int i = l, j = m+1;

        int k = l;
        while (i <= m && j <= r) {
            if (Lpoints[i-l] >= Rpoints[j-(m+1)]) {
                points.set(k, Lpoints[i-l]);
                students.set(k,Lstudents[i-l]);
                i++;
                k++;
            }
            else {
                points.set(k, Rpoints[j-(m+1)]);
                students.set(k, Rstudents[j-(m+1)]);
                j++;
                k++;
            }
        }

        //jhjghj
        /* Copy remaining elements of L[] if any */
        while (i <= m) {
            points.set(k, Lpoints[i-l]);
            students.set(k,Lstudents[i-l]);
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j <= l) {
            points.set(k,Rpoints[j-(m+1)]);
            students.set(k,Rstudents[j-(m+1)]);
            j++;
            k++;
        }
    }
    void sort(List<Integer> points,List<Student> students, int l, int r)
    {
        if(r <= l){
            return;
        }
        if (l < r) {
            // Find the middle point
            int m = (r + l) / 2;

            // Sort first and second halves
            sort(points,students, l, m);
            sort(points,students, m + 1, r);

            // Merge the sorted halves
            merge(points, students, l, m, r);
        }
    }

    private Integer similarityIndex(Student student1, Student student2) {
        int points = 0;
        if(student1.getMajor()!=null && student2.getMajor()!=null) {
            if (student1.getMajor().equals(student2.getMajor())) {
                points += 2;
            }
        }

        if(student1.getYear()!=null && student2.getYear()!=null) {
            if (student1.getYear().equals(student2.getYear())) {
                points += 1;
            }
        }

        if(student1.getCampus()!=null && student2.getCampus()!=null) {
            if (student1.getCampus().equals(student2.getCampus())) {
                points += 2;
            }
        }

        if(student1.getGenderPreference()!=null && student2.getGenderPreference()!=null) {
            if (student1.getGenderPreference().equals(student2.getGenderPreference())) {
                points += 2;
            }
        }

        if(student1.getCourse()!=null && student2.getCourse()!=null) {
            for(int i=0;i<student1.getCourse().size();i++)  {
                for(int j=0;j<student2.getCourse().size();j++) {
                    if(student1.getCourse().get(i).equals(student2.getCourse().get(j))) {
                        points+=3;
                        break;
                    }
                }
            }
        }

        return points;

    }

    public List<Student> matchStudent (String userName) {

        Student student = getStudent(userName);
        if(student==null) {
            return null;
        }
        //list of all students
        List<Student> allStudents = getAllStudents();

        //points array
        List<Integer> points = new ArrayList<>();

        for(int i=0;i<allStudents.size();i++) {
            if(allStudents.get(i).getUserName().equals(student.getUserName())) {
                allStudents.remove(i);
                break;
            }
        }

        for(int i = 0; i < allStudents.size(); i++){
            points.add(similarityIndex(student, allStudents.get(i)));
        }

        sort(points,allStudents,0,allStudents.size()-1);



        return allStudents;


    }


    public boolean matchExists(String userName1, String userName2) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("id").is(userName1 + "+" + userName2),
                Criteria.where("id").is(userName2+ "+" + userName1)
        ));

        List<Matches> matches = mongoTemplate.find(query, Matches.class);

        if (matches.isEmpty()){
            return false;
        }

        return true;
    }

    public boolean addMatches(Matches match) {

        if(match.getId() == null || match.getUserOneId() == null || match.getUserTwoId() == null){
            return false;
        }

        if(!match.getId().equals(match.getUserOneId() + "+" + match.getUserTwoId())){
            return false;
        }

        if(matchExists(match.getUserOneId(), match.getUserTwoId())){
            return false;
        }

        Query query2 = new Query();
        query2.addCriteria(new Criteria().orOperator(
                Criteria.where("userName").is(match.getUserOneId()),
                Criteria.where("userName").is(match.getUserTwoId())
        ));

        List<Student> students = mongoTemplate.find(query2, Student.class);

        System.out.println(students);

        if(students.size() != 2){
            return false;
        }

        matchRepository.insert(match);
        return true;
    }


}
