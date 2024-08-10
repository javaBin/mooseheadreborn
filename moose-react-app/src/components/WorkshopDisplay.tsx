import {WorkshopServerType, WorkshopType} from "../ServerTypes";

interface WorkshopDisplayProps {
    workshop:WorkshopType,
    displayLink:boolean,
}

const WorkshopDisplay = (props: WorkshopDisplayProps) => {
    const { workshop, displayLink } = props;
    const titleText = (workshop.workshopType === WorkshopServerType.KIDS) ?
        "JZ Kids: " + workshop.name : workshop.name
    return (<div>
        {displayLink && <a href={"/workshop/" + workshop.id}><h2>{titleText}</h2></a>}
        {!displayLink && <h2>{titleText}</h2>}
        <p>{workshop.workshopStatusText}</p>


    </div>);

};
export default WorkshopDisplay;