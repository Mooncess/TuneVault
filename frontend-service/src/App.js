import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import LoginPage from './pages/LoginPage';
import RegistrationPage from './pages/RegistrationPage';
import CatalogPage from './pages/CatalogPage';
import ResourceDetailsPage from './pages/ResourceDetailsPage';
import ConfirmPage from './pages/ConfirmPage';
import ProfilePage from './pages/ProfilePage';
import MusicResourceManagmentPage from './pages/MusicResourceManagmentPage';
import AdminPanelPage from './pages/AdminPanelPage';
import axios from 'axios';
import MusicResourceCreatePage from './pages/MusicResourceCreatePage';
import ListProducerPage from './pages/ListProducerPage';
import MusicResourceEditPage from "./pages/MusicResourceEditPage";
import ProducerDetailPage from "./pages/ProducerDetailPage";
import AdminLoginPage from "./pages/AdminLoginPage";
import ClaimPage from './pages/ClaimPage';

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
        <Route path="/admin/panel" element={<AdminPanelPage />} />
        <Route path="/admin/login" element={<AdminLoginPage />} />
        <Route path="/producer/" element={<ListProducerPage />} />
        <Route path="/music-resource-managment/edit" element={<MusicResourceEditPage />} />
        <Route path="/producer/:id" element={<ProducerDetailPage />} />
        <Route path="/admin/claim/:id" element={<ClaimPage />} />
      </Routes>
    </Router>
  );
}

export default App;
