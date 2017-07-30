package editor;

public class Print {

	public static void print(String s) {
		if (Editor.args.length == 2) {
            if (Editor.args[1].equals("debug")) {
                System.out.println(s);
            }
        }
	}

}