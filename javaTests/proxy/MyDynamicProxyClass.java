import java.lang.reflect.*;

public class MyDynamicProxyClass implements java.lang.reflect.InvocationHandler {
	Object obj;

	public MyDynamicProxyClass(Object obj) {
		this.obj = obj;
	}

	public Object invoke(Object proxy, Method m, Object[] args)
			throws Throwable {
		try {
			System.out.println("invoke() proxy = " + proxy + "\nmethod = " + m);
		} catch (Exception e) {
			throw e;
		}
		return proxy;
	}
}

interface MyProxyInterface {
	public Object MyMethod();
}

class TestObject {
	public TestObject() {
		System.out.println("TestObject() constructor");
	}

	public void runTest() {
		System.out.println("TestObject() run test ");
	}
}
