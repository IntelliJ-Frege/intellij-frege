package hello;

import hello.Binding;

public class BindingUsage {
    public static void main(String[] args) {
        var result = Binding.sayHel<caret>lo("world");
        System.out.println(result);
    }
}
