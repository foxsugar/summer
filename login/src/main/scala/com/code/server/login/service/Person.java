package com.code.server.login.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Created by sunxianping on 2017/5/10.
 */
public class Person {
    public int id;
    public String name;
    public int age;

    public Person(int id,String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Person setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Person setAge(int age) {
        this.age = age;
        return this;
    }

    public Person(){}

    private void test(){


    }    public static void main(String[] args) {
        final List<Integer> list = new ArrayList<>();

        Stream<Person> s = Stream.of(new Person(1,"1"), new Person(1,"1"), new Person(3,"1"), new Person(4,"1"));

        Stream<Integer> s1 = Stream.of(1, 3, 4, 5, 6);
        List<Person> l = s.collect(toList());
//        Map<Integer,String> n = l.stream().collect(toMap(Person::getId,Person::getName));
        Map<Integer,Long> n1 = l.stream().collect(Collectors.groupingBy(Person::getId,Collectors.counting()));
        Map<Integer,Integer> n2 = l.stream().collect(Collectors.groupingBy(Person::getId,Collectors.summingInt(Person::getAge)));
//        Map<Integer,List<Person>> n3 = l.stream().collect(Collectors.groupingBy(Person::getId,Collectors.toList()));
//        System.out.println(n);
        System.out.println(n1);
        System.out.println(n2);
//        System.out.println(n3);

        Map<Integer,Integer> m1 = s1.collect(toMap(a->a,a->a));
        System.out.println(m1);
    }
}
