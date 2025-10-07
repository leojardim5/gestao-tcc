import * as React from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, FormProvider, UseFormReturn, FieldValues, SubmitHandler } from "react-hook-form"
import { z } from "zod"

interface FormProps<T extends FieldValues> extends Omit<React.ComponentProps<"form">, "onSubmit"> {
  schema: z.ZodSchema<T>;
  onSubmit: SubmitHandler<T>;
  children: (methods: UseFormReturn<T>) => React.ReactNode;
  defaultValues?: UseFormReturn<T>["formState"]["defaultValues"];
}

export const Form = <T extends FieldValues>({
  schema,
  onSubmit,
  children,
  defaultValues,
  ...props
}: FormProps<T>) => {
  const methods = useForm<T>({ resolver: zodResolver(schema), defaultValues });

  return (
    <FormProvider {...methods}>
      <form onSubmit={methods.handleSubmit(onSubmit)} {...props}>
        {children(methods)}
      </form>
    </FormProvider>
  );
};
