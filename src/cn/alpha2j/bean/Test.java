package cn.alpha2j.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by cn.alpha2j on 2017/2/26.
 */
public class Test {

    private List<Integer> minList;
    private Stack<Integer> stack;

    public Test() {
        this.minList = new ArrayList<>();
        this.stack = new Stack<>();
    }

    public void push(Integer integer) {
        stack.push(integer);
        minList.add(integer);
        Collections.sort(minList);
    }

    public Integer pop() {
        Integer removeValue = stack.pop();
        minList.remove(removeValue);
        return removeValue;
    }

    public Integer getMin() {
        return this.minList.get(0);
    }


    public static void main(String[] args) {
        Test test = new Test();
        test.push(4);
        test.push(5);
        test.push(2);
        test.push(1);
        test.push(10);
        test.pop();
        test.pop();
        test.push(0);
        System.out.println(test.getMin());



    }
}
