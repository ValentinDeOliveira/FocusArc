export type ArcStatus = 'ACTIVE' | 'COMPLETED' | 'ARCHIVED';

export interface Arc {
  id: string;
  owner: string;
  name: string;
  totalEstimatedMinutes: number;
  totalCompletedMinutes: number;
  status: ArcStatus;
}

export interface ArcCreationDto {
    name: string;
    totalEstimatedMinutes: number;
}

export interface ArcUpdateDto {
    name: string;
    totalEstimatedMinutes: number;
}
