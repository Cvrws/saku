package net.minecraft.util;

import lombok.Setter;

@Setter
public class Tuple<A, B>
{
    private A first;
    private B second;

    public Tuple(A aIn, B bIn)
    {
        this.first = aIn;
        this.second = bIn;
    }

    public A getFirst()
    {
        return this.first;
    }

    public B getSecond()
    {
        return this.second;
    }
}
