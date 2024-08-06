import {Container} from "react-bootstrap";
import {useEffect, useState} from "react";
import {AdminWorkshopType} from "../ServerTypes";
import readAdminSummary from "../hooks/readAdminSummary";
import AdminWorkshopComponent from "../components/AdminWorkshopComponent";

const AdminSummaryPage = () => {
    const [workshopList,setWorkshopList] = useState<AdminWorkshopType[]>([]);
    useEffect(() => {
        readAdminSummary("de4503a7-fadb-4d23-9085-dc15b86d5320")
            .then((adminSummary) => setWorkshopList(adminSummary.workshopList));
    },[])
    return (<Container>
        <h1>Admin summary</h1>
        {workshopList.map((workshop) => <AdminWorkshopComponent workshop={workshop} key={workshop.id}/>)}
    </Container>)
};

export default AdminSummaryPage;