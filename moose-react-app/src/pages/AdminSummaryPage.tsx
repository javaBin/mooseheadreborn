import {Alert, Container} from "react-bootstrap";
import {useContext, useEffect, useState} from "react";
import {AdminWorkshopType, UserLogin} from "../ServerTypes";
import readAdminSummary from "../hooks/readAdminSummary";
import AdminWorkshopComponent from "../components/AdminWorkshopComponent";
import {AppContext} from "../context/AppContext";

const AdminSummaryPage = () => {
    const [workshopList,setWorkshopList] = useState<AdminWorkshopType[]>([]);

    const appContext = useContext(AppContext);
    const userLogin:UserLogin|null = appContext?.userLogin || null;
    const [errormessage,setErrormessage] = useState<string|null>(null);

    useEffect(() => {
        if (!userLogin?.accessToken) {
            setWorkshopList([]);
            return;
        }
        readAdminSummary(userLogin.accessToken)
            .then((adminSummary) => setWorkshopList(adminSummary.workshopList))
            .catch(errorFromServer => setErrormessage(errorFromServer));


    },[userLogin])
    return (<Container>
        <h1>Admin summary</h1>
        <p><a href={"/collisionSummary"}>Collision summary</a> <a href={"/entryRegistration"}>Checkin page</a> </p>
        {userLogin && workshopList.map((workshop) => <AdminWorkshopComponent workshop={workshop} userLogin={userLogin} key={workshop.id}/>)}
        {errormessage && <Alert variant={"danger"}>{errormessage}</Alert> }
    </Container>)
};

export default AdminSummaryPage;