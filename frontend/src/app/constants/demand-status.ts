export interface Status {
  name: string;
  value: number;
}

export const DEMAND_STATUS: Status[] = [
  {
    name: 'Created',
    value: 0
  },
  {
    name: 'Active',
    value: 1
  },
  {
    name: 'Stopped',
    value: 2
  },
  {
    name: 'Finished',
    value: 3
  },
  {
    name: 'Canceled',
    value: 4
  }
];
