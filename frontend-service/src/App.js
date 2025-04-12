import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import './App.css';
import LoginPage from './pages/LoginPage';
import RegistrationPage from './pages/RegistrationPage';
import CatalogPage from './pages/CatalogPage';
import ResourceDetailsPage from './pages/ResourceDetailsPage';
import ConfirmPage from './pages/ConfirmPage';
import ProfilePage from './pages/ProfilePage';
import MusicResourceManagmentPage from './pages/MusicResourceManagmentPage';
import axios from 'axios';
import MusicResourceCreatePage from './pages/MusicResourceCreatePage';

axios.defaults.withCredentials = true;


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<CatalogPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registration" element={<RegistrationPage />} />
        <Route path="/music-resource/:id" element={<ResourceDetailsPage />} />
        <Route path="/confirm-page/:id" element={<ConfirmPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/music-resource-managment" element={<MusicResourceManagmentPage />} />
        <Route path="/music-resource/create" element={<MusicResourceCreatePage />} />
      </Routes>
    </Router>
  );
}

export default App;
