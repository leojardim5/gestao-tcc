import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useCallback } from 'react';

export const useQueryParams = () => {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const createQueryString = useCallback(
    (paramsToUpdate: Record<string, string | number | undefined>) => {
      const params = new URLSearchParams(searchParams.toString());
      Object.entries(paramsToUpdate).forEach(([name, value]) => {
        if (value === undefined || value === '') {
          params.delete(name);
        } else {
          params.set(name, String(value));
        }
      });

      return params.toString();
    },
    [searchParams]
  );

  const setQueryParams = useCallback(
    (paramsToUpdate: Record<string, string | number | undefined>) => {
      const queryString = createQueryString(paramsToUpdate);
      router.push(`${pathname}?${queryString}`);
    },
    [createQueryString, pathname, router]
  );

  return { searchParams, setQueryParams };
};
