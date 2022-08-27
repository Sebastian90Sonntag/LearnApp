package com.graphicdesigncoding.learnapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

//COPYRIGHT BY GraphicDesignCoding
public class UserList<E>  implements List<E> {

        private UserItem<E> head;
        private static class UserItem<E> {
                private E element;
                private UserItem<E> next;

                public UserItem(E element, UserItem<E> next) {
                        this.element = element;
                        this.next = next;
                }

                // ...
        }
        @Override
        public int size() {
                return 0;
        }

        @Override
        public boolean isEmpty() {
                return false;
        }

        @Override
        public boolean contains(@Nullable Object o) {
                return false;
        }

        @NonNull
        @Override
        public Iterator<E> iterator() {
                return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
                return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] ts) {
                return null;
        }

        @Override
        public boolean add(E e) {
                return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
                return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> collection) {
                return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends E> collection) {
                return false;
        }

        @Override
        public boolean addAll(int i, @NonNull Collection<? extends E> collection) {
                return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> collection) {
                return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> collection) {
                return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public E get(int i) {
                return null;
        }

        @Override
        public E set(int i, E e) {
                return null;
        }

        @Override
        public void add(int i, E e) {

        }

        @Override
        public E remove(int i) {
                return null;
        }

        @Override
        public int indexOf(@Nullable Object o) {
                return 0;
        }

        @Override
        public int lastIndexOf(@Nullable Object o) {
                return 0;
        }

        @NonNull
        @Override
        public ListIterator<E> listIterator() {
                return null;
        }

        @NonNull
        @Override
        public ListIterator<E> listIterator(int i) {
                return null;
        }

        @NonNull
        @Override
        public List<E> subList(int i, int i1) {
                return null;
        }



}
