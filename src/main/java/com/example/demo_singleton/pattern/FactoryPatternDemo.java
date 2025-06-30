package com.example.demo_singleton.pattern;

public class FactoryPatternDemo {
    public static void main(String[] args) {
        FactoryPattern factoryPattern = new FactoryPattern();
        factoryPattern.getInstance("A").produce();
        factoryPattern.getInstance("B").produce();
    }
}

 class FactoryPattern {

    public    produce getInstance(String type){
        if(type.equals("A")){
            return new productA();
        }else if(type.equals("B")){
            return new productB();
        }else{
            return null;
        }
    }

}

interface produce{
	void produce();
}


class productA implements produce{

    @Override
    public void produce() {
        System.out.println("productA");
    }
}

class productB implements produce{
    @Override
    public void produce() {
        System.out.println("productB");
    }
}
