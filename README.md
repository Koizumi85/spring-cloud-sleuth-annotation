# spring-cloud-sleuth-annotation

## Introduction
This extension to spring-cloud-sleuth gives you the ability to create "local" spans at points not instrumented by spring-cloud-sleuth by default.
For example you could want to create local spans for database calls.
With this extension you can create a new Span simply by adding an annotation to your method.

### Example

The following method would be wrapped by a local span with name "TestClass/findProducts"
```
public class TestClass {
...
  @CreateSleuthSpan
  public List<Products> findProducts(Long tenantId, Integer minAmount) {
  ...
  }
}
```

## Further features

### Customize span names

You can customize the name of the created span by adding the "name" attribute to `@CreateSleuthSpan` annotation.
Example: The following will change the span name to "psql/findProductsByTenantAndAmount".

```
public class TestClass {
...
  @CreateSleuthSpan(name = "psql/findProductsByTenantAndAmount")
  public List<Products> findProducts(Long tenantId, Integer minAmount) {
  ...
  }
}
```

### Add tags to span

There are 3 different ways to add tags to a span. All of them are controlled by the `SleuthSpanTag` annotation. Precedence is Way 3 -> Way 2 -> Way 1

#### Way 1: using toString method

The following method adds a tag with name "tenantId" and value of "tenantId.toString()" to the span:

```
public class TestClass {
...
  @CreateSleuthSpan(name = "psql/findProductsByTenantAndAmount")
  public List<Products> findProducts(@SleuthSpanTag("tenantId") Long tenantId, Integer minAmount) {
  ...
  }
}
```

#### Way 2: using SpEL expression for value

The following method adds a tag with name "tenantId" and value of "tenantId + 1" to the span:

```
public class TestClass {
...
  @CreateSleuthSpan(name = "psql/findProductsByTenantAndAmount")
  public List<Products> findProducts(@SleuthSpanTag(value = "tenantId", tagValueExpression = "param + 1") Long tenantId, Integer minAmount) {
  ...
  }
}
```

#### Way 3: use custom SleuthTagValueResolver Bean

The value of the tag for following method will be computed by an implementation of SleuthTagValueResolver interface.

```
public class TestClass {
...
  @CreateSleuthSpan(name = "psql/findProductsByTenantAndAmount")
  public List<Products> findProducts(@SleuthSpanTag(value = "tenantId", tagValueResolverBeanName = "tenantIdTagValueResolver") Long tenantId, Integer minAmount) {
  ...
  }
}
```
