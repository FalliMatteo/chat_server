package com.example;

public class Semaforo {
    private boolean wait;

    public Semaforo(){
        this.wait = false;
    }

    public synchronized void p(){
        while(wait){
            try {
                wait();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        wait = true;
    }

    public synchronized void v(){
        wait = false;
        notify();
    }
}
