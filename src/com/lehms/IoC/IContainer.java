package com.lehms.IoC;

public interface IContainer {
	<T> T resolve(Class<T> annotationType);
}
