export type ArcStatus = 'ACTIVE' | 'COMPLETED' | 'ARCHIVED';

export interface Arc {
  id: string;
  owner: string;
  name: string;
  totalEstimatedMinutes: number;
  totalCompletedMinutes: number;
  status: ArcStatus;
  startDate: string;
  endDate: string;
}

export interface ArcCreationDto {
    name: string;
    totalEstimatedMinutes: number;
}

export interface ArcUpdateDto {
    name: string;
    totalEstimatedMinutes: number;
}

export interface ArcSummaryResponseDto {
    totalEstimatedMinutes: number,
    totalCompletedMinutes: number,
    remainingMinutes: number,
    nbChapterCompleted: number,
    nbChapterPlanned: number,
    nbChapterSkipped: number,
    daysStreak: number
}
