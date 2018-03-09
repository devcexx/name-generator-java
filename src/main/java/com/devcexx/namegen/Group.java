package com.devcexx.namegen;

import java.util.Objects;

public class Group {
    public final String group;
    public final boolean start;
    public final boolean end;

    public Group(String group, boolean start, boolean end) {
        this.group = group;
        this.start = start;
        this.end = end;
    }

    public String groupName() {
        String name = group;
        if (start) {
            name = "!" + name;
        }

        if (end) {
            name += "!";
        }

        return name;
    }

    public static Group fromGroupName(String name) {
        boolean start;
        boolean end;
        if (name.startsWith("!")) {
            start = true;
            name = name.substring(1);
        } else start = false;

        if (name.endsWith("!")) {
            end = true;
            name = name.substring(0, name.length() - 1);
        } else end = false;

        return new Group(name, start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group1 = (Group) o;
        return start == group1.start &&
                end == group1.end &&
                Objects.equals(group, group1.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, start, end);
    }

    @Override
    public String toString() {
        return group;
    }
}
