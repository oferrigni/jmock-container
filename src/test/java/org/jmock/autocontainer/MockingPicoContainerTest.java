package org.jmock.autocontainer;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;

public class MockingPicoContainerTest {
	private DefaultPicoContainer delegate;
	private Mockery context;
	@Before
	public void create_mocks(){
		delegate = new DefaultPicoContainer(new Caching());
		context = new Mockery();
		context.setImposteriser(ClassImposteriser.INSTANCE);
	}

	@Test
	public void single_arg_ctor() throws Exception {
		MockingPicoContainer<ToBeMocked1> autoMocked = new MockingPicoContainer<ToBeMocked1>(delegate, context, ToBeMocked1.class);
		assertSame(delegate.getComponent(Mock1.class),autoMocked.sut.arg1);
	}
	
	@Test
	public void two_arg_ctor_greedy() throws Exception {
		MockingPicoContainer<ToBeMocked2> autoMocked = new MockingPicoContainer<ToBeMocked2>(delegate, context, ToBeMocked2.class);
		assertSame(delegate.getComponent(Mock1.class),autoMocked.sut.arg1);
		assertSame(delegate.getComponent(Mock2.class),autoMocked.sut.arg2);
	}	
	@Test
	public void three_arg_ctor_greedy() throws Exception {
		MockingPicoContainer<ToBeMocked3> autoMocked = new MockingPicoContainer<ToBeMocked3>(delegate, context, ToBeMocked3.class);
		assertSame(delegate.getComponent(Mock1.class),autoMocked.sut.arg1);
		assertSame(delegate.getComponent(Mock2.class),autoMocked.sut.arg2);
		assertSame(delegate.getComponent(Mock3.class),autoMocked.sut.arg3);
	}
	@Test
	public void verify() throws Exception {
		final MockingPicoContainer<Foo1> autoMocked = new MockingPicoContainer<Foo1>(delegate, context, Foo1.class);
		assertSame(delegate.getComponent(Mock1.class),autoMocked.sut.arg1);
		autoMocked.checking(new Expectations(){{ one(autoMocked.get(Mock1.class)).baz();}});
		autoMocked.sut.bar();
		autoMocked.verify();
	}

	public static class Mock1 {
		public void baz() {
		}
	}
	public static class Mock2 {
	}
	public static class Mock3 {
	}
	
	public static class ToBeMocked1 {
		public final Mock1 arg1;

		public ToBeMocked1(Mock1 arg1){
			this.arg1 = arg1;
		}
	}
	public static class ToBeMocked2 {
		public  Mock1 arg1;
		public  Mock2 arg2;
		public ToBeMocked2(Mock1 arg1){
			this.arg1 = arg1;
		}
		public ToBeMocked2(Mock1 arg1, Mock2 arg2){
			this.arg1 = arg1;
			this.arg2 = arg2;
		}
	}
	public static class ToBeMocked3 {
		public  Mock1 arg1;
		public  Mock2 arg2;
		public  Mock3 arg3;
		public ToBeMocked3(Mock1 arg1){
		}
		public ToBeMocked3(Mock1 arg1, Mock2 arg2){
		}
		public ToBeMocked3(Mock1 arg1, Mock2 arg2, Mock3 arg3){
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.arg3 = arg3;
		}
	}
	
	public static class Foo1 {
		private final Mock1 arg1;

		public Foo1(Mock1 arg1) {
			this.arg1 = arg1;
		}
		
		public void bar() {
			arg1.baz();
		}
	}
}
