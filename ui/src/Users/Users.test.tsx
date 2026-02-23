import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { Users } from "./Users";
import { User } from "./User";
import * as HttpUtil from "../Commons/Http.util";
import { PaginationHeaders } from "../Commons/PaginationHeaders";

jest.mock("../Commons/Http.util");

const mockUsers: User[] = Array.from({ length: 25 }, (_, i) => ({
  firstName: `First${i + 1}`,
  lastName: `Last${i + 1}`,
  emailAddress: `user${i + 1}@example.com`
}));

const createMockResponse = (users: User[], totalCount: number, pageNumber: number, pageSize: number, totalPages: number) => ({
  data: users,
  headers: {
    [PaginationHeaders.TOTAL_COUNT]: totalCount.toString(),
    [PaginationHeaders.PAGE_NUMBER]: pageNumber.toString(),
    [PaginationHeaders.PAGE_SIZE]: pageSize.toString(),
    [PaginationHeaders.TOTAL_PAGES]: totalPages.toString()
  }
});

describe('Users Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders users table with pagination', async () => {
    const firstPageUsers = mockUsers.slice(0, 10);
    (HttpUtil.httpGetWithHeaders as jest.Mock).mockResolvedValue(
      createMockResponse(firstPageUsers, 25, 0, 10, 3)
    );

    render(<Users />);

    await waitFor(() => {
      expect(screen.getByText('Users')).toBeInTheDocument();
      expect(screen.getByText('First Name')).toBeInTheDocument();
      expect(screen.getByText('Last Name')).toBeInTheDocument();
      expect(screen.getByText('Email Address')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.getByText('First1')).toBeInTheDocument();
      expect(screen.getByText('Page 1 of 3')).toBeInTheDocument();
    });
  });

  it('renders without pagination when items are less than page size', async () => {
    const fewUsers = mockUsers.slice(0, 5);
    (HttpUtil.httpGetWithHeaders as jest.Mock).mockResolvedValue(
      createMockResponse(fewUsers, 5, 0, 10, 1)
    );

    render(<Users />);

    await waitFor(() => {
      expect(screen.getByText('First1')).toBeInTheDocument();
    });

    expect(screen.queryByText(/Page/)).not.toBeInTheDocument();
  });
});