/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls;

import java.util.Objects;

/**
 *
 * @author sven
 */
public class Word implements Comparable<Word> {
    private String text;
    private double weight;

    public Word(String text, double weight) {
        this.text = text;
        this.weight = weight;
    }
    
    public String getText() {
        return text;
    }

    @Override
    public int compareTo(Word o) {
        return ((Double) weight).compareTo(o.weight);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Word{" + "text=" + text + ", weight=" + weight + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.text);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Word other = (Word) obj;
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        return true;
    }
    

    
}
