package com.oliver.workout.trainer.test.support;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.AbstractDelegatingMutablePicoContainer;

public class MockingPicoContainer<M> extends
		AbstractDelegatingMutablePicoContainer {

	public final Mockery context;
	public M sut;

	public MockingPicoContainer(Class<M> sutClazz)  {
		this(new DefaultPicoContainer(new Caching()), new Mockery(), sutClazz);
	}

	MockingPicoContainer(MutablePicoContainer delegate, Mockery context) {
		super(delegate);
		context.setImposteriser(ClassImposteriser.INSTANCE);
		this.context = context;
	}

	MockingPicoContainer(DefaultPicoContainer delegate, Mockery context,
			Class<M> sutClazz){
		super(delegate);
		context.setImposteriser(ClassImposteriser.INSTANCE);
		this.context = context;
		try {
			generateSut(sutClazz);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public M generateSut(Class<M> clazz) throws Exception {
		Constructor<M>[] constructors = (Constructor<M>[]) clazz
				.getConstructors();
		Constructor<M> widest = null;
		int paramCount = 0;
		for (Constructor<M> constructor : constructors) {
			if (paramCount < constructor.getParameterTypes().length) {
				paramCount = constructor.getParameterTypes().length;
				widest = constructor;
			}
		}
		if (widest != null) {
			Class<?>[] parameterTypes = widest.getParameterTypes();
			List<Object> args = new ArrayList<Object>();
			for (Class<?> class1 : parameterTypes) {
				if (this.getComponents(class1).isEmpty()) {
					Object mock = context.mock(class1);
					this.addComponent(class1, mock);
					args.add(mock);
				}
			}

			sut = widest.newInstance((Object[]) args.toArray(new Object[args
					.size()]));
		} else {
			sut = clazz.newInstance();
		}
		return sut;
	}

	public <T> T get(Class<T> componentType) {
		return getComponent(componentType);
	}

	@Override
	public <T> T getComponent(Class<T> componentType) {
		if (this.getComponents(componentType).isEmpty()) {
			Object mock = context.mock(componentType);
			this.addComponent(componentType, mock);
		}
		return super.getComponent(componentType);
	}

	public void checking(Expectations expectations) {
		context.checking(expectations);
	}

	public void verify() {
		context.assertIsSatisfied();
	}
}
