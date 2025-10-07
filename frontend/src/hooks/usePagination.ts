import { useState, useMemo } from 'react';

interface PaginationProps {
  totalElements: number;
  pageSize: number;
}

export const usePagination = ({ totalElements, pageSize }: PaginationProps) => {
  const [currentPage, setCurrentPage] = useState(1);

  const totalPages = useMemo(() => {
    return Math.ceil(totalElements / pageSize);
  }, [totalElements, pageSize]);

  const goToPage = (page: number) => {
    setCurrentPage(Math.max(1, Math.min(totalPages, page)));
  };

  const nextPage = () => {
    setCurrentPage((prev) => Math.min(totalPages, prev + 1));
  };

  const prevPage = () => {
    setCurrentPage((prev) => Math.max(1, prev - 1));
  };

  return {
    currentPage,
    totalPages,
    goToPage,
    nextPage,
    prevPage,
    canGoNext: currentPage < totalPages,
    canGoPrev: currentPage > 1,
  };
};
