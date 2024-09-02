import React from 'react';
import './App.css';
import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";
import MainListingPage from "./pages/MainListingPage";
import 'bootstrap/dist/css/bootstrap.min.css';
import WorkshopRegistryPage from "./pages/WorkshopRegistryPage";
import ActivateParticipantPage from "./pages/ActivateParticipantPage";
import RegistrationViewPage from "./pages/RegistrationViewPage";
import TopNavbarComponent from "./components/TopNavbarComponent";
import {AppContextProvider} from "./context/AppContextProvider";
import AdminSummaryPage from "./pages/AdminSummaryPage";
import AdminLoginPage from "./pages/AdminLoginPage";
import {Navbar} from "react-bootstrap";
import PrivacyPage from "./pages/PrivacyPage";
import CollisionSummaryPage from "./pages/CollisionSummaryPage";
import ParticipantViewPage from "./pages/ParticipantViewPage";
import EntryRegistrationPage from "./pages/EntryRegistrationPage";

function App() {
  return (
    <div>
        <AppContextProvider>
            <TopNavbarComponent/>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<MainListingPage/>} />
                    <Route path="/workshop/:workshopId" element={<WorkshopRegistryPage/>} />
                    <Route path="/activate/:registerKey" element={<ActivateParticipantPage/>} />
                    <Route path="/registration/:registrationId" element={<RegistrationViewPage/>} />
                    <Route path="/participant/:participantId" element={<ParticipantViewPage/>} />
                    <Route path="/admin" element={<AdminSummaryPage/>} />
                    <Route path="/adminlogin" element={<AdminLoginPage/>} />
                    <Route path="/collisionSummary" element={<CollisionSummaryPage/>} />
                    <Route path="/entryRegistration" element={<EntryRegistrationPage/>}/>
                    <Route path="/privacy" element={<PrivacyPage/>} />
                </Routes>
           </BrowserRouter>
            <Navbar bg="dark" variant="dark" fixed="bottom">
                <Navbar.Text>&nbsp;<a href={"/privacy"}>Privacy</a></Navbar.Text>
                <Navbar.Text className="ms-auto">© 2024 JavaZone&nbsp;</Navbar.Text>
            </Navbar>
        </AppContextProvider>
    </div>
  );
}

export default App;
