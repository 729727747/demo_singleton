package com.example.demo_singleton.pattern;

public class AdapterPatternDemo {



}


interface  CourseStudy{

    void study(String course);
}


class MathCourseStudy  implements CourseStudy{

    @Override
    public void study(String course ) {
        System.out.println("学习" + course);
    }
}

class ChinaCourseStudy  implements CourseStudy{

    @Override
    public void study(String course) {
        System.out.println("学习" + course);
    }
}


class CourseStudyAdapter implements CourseStudy{

    @Override
    public void study(String course) {

    }
}



