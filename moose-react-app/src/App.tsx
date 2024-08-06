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
                    <Route path="/admin" element={<AdminSummaryPage/>} />
                </Routes>
           </BrowserRouter>
        </AppContextProvider>
    </div>
  );
}

export default App;
