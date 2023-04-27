package com.ebookfrenzy.lecturenote.Adapters;

import java.util.EmptyStackException;

public class Stack {
    private final int[] arr;
    private int top;

    public Stack(int size) {
        arr = new int[size];
        top = -1;
    }

    public void push(int value) {
        if (top == arr.length - 1) {
            throw new StackOverflowError();
        }
        top++;
        arr[top] = value;
    }

    public int pop() {
        if (top == -1) {
            throw new EmptyStackException();
        }
        int value = arr[top];
        top--;
        return value;
    }

    public int peek() {
        if (top == -1) {
            throw new EmptyStackException();

        }
        return arr[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }
}
