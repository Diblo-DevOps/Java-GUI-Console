# Java GUI Console
This is a standalone Java GUI console that is simple to implement in any console based Java code.

## Usage
1. Download Console.java
2. Place it in your project
3. Create Console as an object
4. Call your codes

_Example_

    public class Foot {
        public static void main(String[] args) {
            Console console = new Console("Your Title", 1280, 1024);
            console.show();

            new Bar().metohod();
        }
    }

or

    public class Foot {
        public static void main(String[] args) {
            Console console = new Console("Your Title", 1280, 1024);
            console.show();

            Bar bar = new Bar();

            boolean finished = false;
            while (finished) {
                finished = bar.metohod();
            }

            console.destroy();
        }
    }

