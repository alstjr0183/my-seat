import { httpClient } from '../api';

export const getReservations = async ({ page, size }: { page: string; size: string }) => {
  const accessToken = 'token';

  const { data } = await httpClient.get(`/admin/reservations?page=${page}&size=${size}`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });

  return data;
};

export const x = () => {};