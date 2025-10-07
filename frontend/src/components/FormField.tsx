import * as React from 'react';
import { useFormContext, Controller, FieldValues, Path } from 'react-hook-form';
import { Input, InputProps } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';

interface FormFieldProps<T extends FieldValues> extends InputProps {
  name: Path<T>;
  label: string;
}

export const FormField = <T extends FieldValues>({
  name,
  label,
  ...props
}: FormFieldProps<T>) => {
  const { control, formState: { errors } } = useFormContext<T>();
  const error = errors[name];

  return (
    <div className="grid gap-2">
      <Label htmlFor={name}>{label}</Label>
      <Controller
        name={name}
        control={control}
        render={({ field }) => <Input id={name} {...field} {...props} />}
      />
      {error && <p className="text-sm font-medium text-destructive">{error.message?.toString()}</p>}
    </div>
  );
};
