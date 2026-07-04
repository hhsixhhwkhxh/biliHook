package hhsixhhwkhxh.bilibili;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class FakeArrayList<E> extends ArrayList<E> {
    public FakeArrayList(){

    }

    @Override
    public boolean add(E e) {
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        return super.addAll(index, c);
    }
}
