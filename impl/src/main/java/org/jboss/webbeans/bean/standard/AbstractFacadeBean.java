package org.jboss.webbeans.bean.standard;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.context.CreationalContext;
import javax.inject.manager.InjectionPoint;

import org.jboss.webbeans.RootManager;
import org.jboss.webbeans.context.DependentContext;

public abstract class AbstractFacadeBean<T> extends AbstractStandardBean<T>
{

   protected AbstractFacadeBean(RootManager manager)
   {
      super(manager);
   }

   public T create(CreationalContext<T> creationalContext)
   {
      try
      {
         DependentContext.INSTANCE.setActive(true);
         //TODO Fix to use IP's manager rather than this bean's
         InjectionPoint injectionPoint = this.getManager().getInjectionPoint();
         Type genericType = injectionPoint.getType();
         if (genericType instanceof ParameterizedType )
         {
            Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (type instanceof Class)
            {
               Class<?> clazz = Class.class.cast(type);
               return newInstance(clazz, fixBindings(injectionPoint.getBindings()));
            }
            else
            {
               throw new IllegalStateException("Must have concrete type argument " + injectionPoint);
            }
         }
         else
         {
            throw new IllegalStateException("Must have concrete type argument " + injectionPoint);
         }
         
      }
      finally
      {
         DependentContext.INSTANCE.setActive(false);
      }
   }
   
   /**
    * Merges and validates the current and new bindings
    * 
    * Checks with an abstract method for annotations to exclude
    * 
    * @param currentBindings Existing bindings
    * @param newBindings New bindings
    * @return The union of the bindings
    */
   protected Set<Annotation> fixBindings(Set<? extends Annotation> bindings)
   {
      Set<Annotation> result = new HashSet<Annotation>();
      for (Annotation newAnnotation : bindings)
      {
         if (!getFilteredAnnotationTypes().contains(newAnnotation.annotationType()))
         {
            result.add(newAnnotation);
         }
      }
      return result;
   }
   
   public void destroy(T instance)
   {
      // TODO Auto-generated method stub
   }

   /**
    * Gets a set of annotation classes to ignore
    * 
    * @return A set of annotation classes to ignore
    */
   protected abstract Set<Class<? extends Annotation>> getFilteredAnnotationTypes();
   
   protected abstract T newInstance(Class<?> clazz, Set<Annotation> annotations);
   
}
