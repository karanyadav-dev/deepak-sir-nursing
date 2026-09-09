import { useEffect, useState } from 'react';
import ClassroomView from '../components/classroom/ClassroomView';
import { useClassroomStore } from '../store/classroomStore';

export default function Classroom() {
  const { session } = useClassroomStore();

  return (
    <div className="h-screen flex flex-col">
      <ClassroomView />
    </div>
  );
}