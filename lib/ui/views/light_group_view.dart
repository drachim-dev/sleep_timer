import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/ui/views/light_group_viewmodel.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:stacked/stacked.dart';

class LightGroupView extends StatefulWidget {
  @override
  _LightGroupViewState createState() => _LightGroupViewState();
}

class _LightGroupViewState extends State<LightGroupView> {
  LightGroupViewModel model;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ViewModelBuilder<LightGroupViewModel>.reactive(
        viewModelBuilder: () => LightGroupViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Scaffold(
            appBar: AppBar(title: Text(S.of(context).titleLightGroups)),
            body: _buildBody(theme),
            floatingActionButton: _buildFab(theme),
          );
        });
  }

  Widget _buildBody(final ThemeData theme) {
    if (model.isBusy) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: kVerticalPadding),
            Text(S.of(context).stateSearching),
          ],
        ),
      );
    }

    // No saved bridges found
    if (model.data == null || model.data.isEmpty) {
      return _buildNoResults(theme);
    }

    // build rooms for each connected bridge
    return ListView.builder(
      itemBuilder: (_, int index) {
        final BridgeModel bridgeModel = model.data[index];
        return _buildRoomsForBridge(bridgeModel);
      },
      itemCount: model.data.length,
    );
  }

  Column _buildRoomsForBridge(
      final BridgeModel bridgeModel) {

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        SectionHeader(
          '${bridgeModel.name}',
          leftPadding: kPreferenceTitleLeftPadding,
        ),
        ListView.separated(
          shrinkWrap: true,
          itemBuilder: (_, int index) {
            final group = bridgeModel.groups[index];

            return SwitchListTile(
              secondary: _buildIconForRoom(group.className),
              title: Text('${group.name}'),
              subtitle: Text(S.of(context).countLights(group.numberOfLights)),
              onChanged: (value) async {
                group.actionEnabled = value;
                model.onChangeRoom(value);
              },
              value: group.actionEnabled,
            );
          },
          itemCount: bridgeModel.groups.length,
          separatorBuilder: (_, int index) => Divider(),
        ),
      ],
    );
  }

  Icon _buildIconForRoom(final String roomType) {
    print(roomType.toLowerCase());
    var icon;
    switch (roomType.toLowerCase()) {
      case 'living room':
        icon = Icons.weekend_outlined;
        break;
      case 'kitchen':
        icon = Icons.kitchen_outlined;
        break;
      case 'dining':
        icon = Icons.local_dining_outlined;
        break;
      case 'bedroom':
        icon = Icons.king_bed_outlined;
        break;
      case 'kids bedroom':
        icon = Icons.single_bed_outlined;
        break;
      case 'bathroom':
        icon = Icons.bathtub_outlined;
        break;
      case 'office':
        icon = Icons.work_outline_outlined;
        break;
      case 'gym':
        icon = Icons.fitness_center_outlined;
        break;
      default:
        icon = Icons.home_outlined;
    }
    return Icon(icon);
  }

  Widget _buildNoResults(final ThemeData theme) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image.asset('assets/img_no_results.webp', width: 200),
          SizedBox(height: kVerticalPadding),
          Text(S.of(context).noLightsFound, style: theme.textTheme.headline6),
          SizedBox(height: kVerticalPaddingBig),
        ],
      ),
    );
  }

  Widget _buildFab(final ThemeData theme) {
    return FloatingActionButton(
      child: Icon(Icons.search_outlined),
      onPressed: model.navigateToLinkBridge,
    );
  }
}
